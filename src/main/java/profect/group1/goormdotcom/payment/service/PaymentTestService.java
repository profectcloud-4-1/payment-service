package profect.group1.goormdotcom.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.PaymentHandler;
import profect.group1.goormdotcom.payment.config.TossPaymentConfig;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.PaymentHistory;
import profect.group1.goormdotcom.payment.infrastructure.client.OrderClient;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentFailResultDto;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentSuccessResultDto;
import profect.group1.goormdotcom.payment.repository.PaymentHistoryRepository;
import profect.group1.goormdotcom.payment.repository.PaymentRepository;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;
import profect.group1.goormdotcom.payment.repository.mapper.PaymentHistoryMapper;
import profect.group1.goormdotcom.payment.repository.mapper.PaymentMapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTestService {
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final ApplicationEventPublisher eventPublisher;
    private final WebClient tossWebClient;
    private final OrderClient orderClient;

    @Transactional
    public PaymentSuccessResponseDto tossPaymentSuccess(PaymentSuccessRequestDto dto, UUID userId) {

        //처리중인 결제내역이 있는지 확인
        if (dto.getOrderId() != null) {
            paymentRepository.findByOrderIdAndStatus(dto.getOrderId(), "PAY0000")
                    .ifPresent(existing -> {
                        throw new PaymentHandler(ErrorStatus._DUPLICATE_PAYMENT_REQUEST);
                    });
        }

        //멱등성 체크-> 이미 처리된 paymentKey인지 확인
        if(paymentRepository.existsByPaymentKey(dto.getPaymentKey())) {
            throw new PaymentHandler(ErrorStatus._DUPLICATE_PAYMENT_KEY);
        }

        //금액 검증
        //if (!paymentEntity.getAmount().equals(dto.getAmount())) {
        //    throw new PaymentHandler(ErrorStatus._INVALID_PAYMENT_AMOUNT);
        //}

        //결제 생성
        Payment payment = Payment.create(userId, dto.getOrderId(), dto.getOrderName(), dto.getPaymentKey(), dto.getAmount());

        //1000원 이하면 결제X
        if(payment.getAmount() < 1000) {
            throw new PaymentHandler(ErrorStatus._BAD_REQUEST);
        }

        // 테스트용
        simulateDelay(); // 토스 결제 성공 확인 요청 . 응답시간 시뮬레이션 (500ms + jitter(300ms)

        PaymentSuccessResponseDto response = new PaymentSuccessResponseDto(
                "MID_1234567890",
                dto.getPaymentKey(),
                dto.getOrderId().toString(),
                dto.getOrderName(),
                "DONE",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                "KRW",
                dto.getAmount().toString(),
                "0",
                dto.getAmount().toString(),
                "CARD"
        );

        //offsetTime 직렬화를 위한 클래스
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String rawResponseJson = null;
        try {
            rawResponseJson = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ignored) {}

        payment.setStatus("PAY0001");

        PaymentEntity savedEntity = paymentRepository.saveAndFlush(PaymentMapper.toEntity(payment));
        payment.setId(savedEntity.getId());

        PaymentHistory history = PaymentHistory.create(
                savedEntity.getId(),
                "PAY0001",                 //결제 승인
                savedEntity.getAmount(),
                savedEntity.getPaymentKey(),
                rawResponseJson,
                "SUCCESS"
        );

        paymentHistoryRepository.save(PaymentHistoryMapper.toEntity(history));

        try {
            orderClient.notifyPaymentSuccessResult(
                    savedEntity.getOrderId(),
                    new PaymentSuccessResultDto(
                            savedEntity.getUserId(),
                            "PAY0001",                //결제 성공
                            savedEntity.getAmount(),
                            response.approvedAt()
                    )
            );

            log.info("[ORDER SYNC] 주문서비스 결제결과 전달 성공: {}", savedEntity.getOrderId());
        } catch (Exception e) {
            log.error("[ORDER SYNC] 주문서비스 결제결과 전달 실패: {}", e.getMessage());
            throw e;
        }
        return response;
    }

    @Transactional
    public void tossPaymentFail(UUID userId, PaymentFailRequestDto dto) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        if(!paymentEntity.getUserId().equals(userId)) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_FORBIDDEN);
        }

        PaymentHistory history = PaymentHistory.create(
                paymentEntity.getId(),
                "PAY0002",                 //결제 실패
                paymentEntity.getAmount(),
                paymentEntity.getPaymentKey(),
                null,
                "FAILED"
        );

        paymentEntity.setStatus("PAY0002");

        paymentRepository.save(paymentEntity);
        paymentHistoryRepository.save(PaymentHistoryMapper.toEntity(history));

        try {
            orderClient.notifyPaymentFailResult(
                    paymentEntity.getOrderId(),
                    new PaymentFailResultDto(
                            "PAY0002",                //결제 실패
                            paymentEntity.getAmount()
                    )
            );

        } catch (Exception e) {
            log.error("[ORDER SYNC] 주문서비스 결제결과 전달 실패: {}", e.getMessage());
            throw e;
        }
    }

    private void simulateDelay() {
        int base = 500;   // 기본 500ms
        int jitter = 300; // ±300ms
        int delay = base + ThreadLocalRandom.current().nextInt(-jitter, jitter + 1);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }
}
