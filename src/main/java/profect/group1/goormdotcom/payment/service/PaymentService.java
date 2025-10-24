package profect.group1.goormdotcom.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.reactive.function.client.WebClient;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.PaymentHandler;
import profect.group1.goormdotcom.common.security.SecurityUtil;
import profect.group1.goormdotcom.payment.config.TossPaymentConfig;
import profect.group1.goormdotcom.payment.controller.dto.request.*;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.controller.mapper.PaymentDtoMapper;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.enums.Status;
import profect.group1.goormdotcom.payment.domain.enums.TossPaymentStatus;
import profect.group1.goormdotcom.payment.repository.PaymentRepository;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;
import profect.group1.goormdotcom.payment.repository.mapper.PaymentMapper;
import profect.group1.goormdotcom.user.domain.User;
import org.springframework.transaction.annotation.Propagation;

import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final ApplicationEventPublisher eventPublisher;
    private final WebClient tossWebClient;

    @Transactional
    public Payment requestPayment(PaymentCreateRequestDto dto, User user) {
        //권한 확인
        /* if(!SecurityUtil.isCustomer()) {
            throw new PaymentHandler(ErrorStatus.INSUFFICIENT_ROLE);
        }*/

        //TODO: order에서 orderId 존재하는지 확인? MSA에서는 처리할 것인지 고민
        //한다면 OrderClient를 통해서 받기, 주문 정보 조회 및 금액 검증

        //처리중인 결제내역이 있는지 확인
        if (dto.getOrderId() != null) {
            paymentRepository.findByOrderIdAndStatus(dto.getOrderId(), Status.PENDING)
                    .ifPresent(existing -> {
                        throw new PaymentHandler(ErrorStatus._DUPLICATE_PAYMENT_REQUEST);
                    });
        }

        //TODO: orderNumber order에서 전달받아야 함
        String orderNumber = dto.getOrderNumber();
        if (orderNumber == null || orderNumber.isBlank()) {
            orderNumber = generateOrderNumber();
        }

        Payment payment = Payment.create(dto.getOrderId(), orderNumber, dto.getOrderName(), dto.getPayType(), dto.getAmount());

        //1000원 이하면 결제X
        if(payment.getAmount() < 1000) {
            throw new PaymentHandler(ErrorStatus._BAD_REQUEST);
        }

        PaymentEntity savedEntity = paymentRepository.save(PaymentMapper.toEntity(payment));
        payment.setId(savedEntity.getId());

        return payment;
    }

    //Order에서 만들어서 넘어와야 함. 임시 구현
    private String generateOrderNumber() {
        var now = java.time.LocalDateTime.now();
        String ts = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String rand = java.util.UUID.randomUUID().toString().substring(0,5).toUpperCase();
        return "ORD-" + ts + "-" + rand;
    }

    @Transactional
    public PaymentSuccessResponseDto tossPaymentSuccess(PaymentSuccessRequestDto dto) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderNumber(dto.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        //멱등성 체크-> 이미 처리된 paymentKey인지 확인
        if(paymentRepository.existsByPaymentKey(dto.getPaymentKey())) {
            return null;
        }

        //히스토리 생성

        //금액 검증
        if (!paymentEntity.getAmount().equals(dto.getAmount())) {
            throw new PaymentHandler(ErrorStatus._INVALID_PAYMENT_AMOUNT);
        }

        String authorizations = "Basic " + Base64.getEncoder().encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes());

        //TODO: RestClient로 바꾸는 것 고려
        PaymentSuccessResponseDto response = tossWebClient.post()
                .uri("/confirm")
                .header("Authorization", authorizations)
                .header("Content-Type", "application/json")
                .bodyValue(java.util.Map.of(
                        "paymentKey", dto.getPaymentKey(),
                        "orderId", dto.getOrderId(),
                        "amount", dto.getAmount()
                ))
                .retrieve()
                .bodyToMono(PaymentSuccessResponseDto.class)
                .block();

        paymentEntity.setPaymentKey(dto.getPaymentKey());
        paymentEntity.setStatus(Status.SUCCESS);

        //TODO: 히스토리 상태를 성공으로 변경
        //실패 시 histrory 실패로 변경..

        return response;
    }

    @Transactional
    public void tossPaymentFail(PaymentFailRequestDto dto) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderNumber(dto.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        paymentEntity.setStatus(Status.FAIL);
    }

    @Transactional
    public void tossPaymentCancel(PaymentCancelRequestDto dto, String paymentKey) {
        PaymentEntity paymentEntity = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        //상태 검증
        if (paymentEntity.getStatus() == Status.CANCEL)
            throw new PaymentHandler(ErrorStatus._ALREADY_CANCELED_REQUEST);
        if (paymentEntity.getStatus() != Status.SUCCESS
                && paymentEntity.getStatus() != Status.PARTIAL_CANCEL) {
            throw new PaymentHandler(ErrorStatus._INVALID_PAYMENT_STATUS);
        }

        //취소 금액 검증
        long refundableAmount = calculateRefundableAmount(paymentEntity);
        Long cancelAmount = dto.getCancelAmount() != null ?
                dto.getCancelAmount() : refundableAmount;

        if (cancelAmount > refundableAmount) {
            throw new PaymentHandler(ErrorStatus._INVALID_CANCEL_AMOUNT);
        }

        // 전액 취소면 전체 금액 설정
        if (dto.getCancelAmount() == null) {
            dto = PaymentCancelRequestDto.builder()
                    .cancelReason(dto.getCancelReason())
                    .cancelAmount(refundableAmount)
                    .build();
        }

        //취소 요청 중 상태로 변경
        paymentEntity.setStatus(Status.CANCEL_PENDING);
        paymentRepository.save(paymentEntity);

        //트랜잭션 커밋 후 실행될 이벤트 -> PG에 결제 취소 요청
        eventPublisher.publishEvent(new PaymentCanceledEvent(paymentEntity.getId(), paymentKey, dto));
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void executeCancel(PaymentCanceledEvent event) {
        String paymentKey = event.paymentKey();
        PaymentCancelRequestDto dto = event.requestDto();

        String authorizations = "Basic " + Base64.getEncoder().encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes());

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", dto.getCancelReason());
        if (dto.getCancelAmount() != null) {
            body.put("cancelAmount", dto.getCancelAmount());
        }

        try {
            //TODO: RestClient로 바꾸는 것 고려
            PaymentCancelResponseDto response = tossWebClient.post()
                    .uri("/{paymentKey}/cancel", paymentKey)
                    .header("Authorization", authorizations)
                    .header("Content-Type", "application/json")
                    //.header("Idempotency-Key", UUID.randomUUID().toString())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(PaymentCancelResponseDto.class)
                    .block();

            //응답 검증
            if (response == null || response.cancels() == null || response.cancels().isEmpty()) {
                throw new PaymentHandler(ErrorStatus._PAYMENT_CANCEL_FAILED);
            }

            PaymentCancelResponseDto.CancelEntry lastCancel = response.cancels().get(response.cancels().size() - 1);

            //취소 상태 확인
            if (!"DONE".equalsIgnoreCase(lastCancel.cancelStatus())) {
                throw new PaymentHandler(ErrorStatus._PAYMENT_CANCEL_FAILED);
            }

            //DB 업데이트 (별도 트랜잭션 메서드 호출)
            updatePaymentCancelStatus(event.paymentId(), lastCancel.cancelAmount());

        } catch (Exception e) {
            //예외 발생 시 Toss 상태 재조회로 동기화 시도
            syncPaymentStatusFromToss(paymentKey);
        }

        //TODO: 히스토리에 취소사유 등록
    }

    public void updatePaymentCancelStatus(UUID paymentId, Long canceledAmount) {
        PaymentEntity paymentEntity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        //현재 취소된 금액 가져오기 (null 안전 처리)
        long currentCanceledAmount = paymentEntity.getCanceledAmount() != null ?
                paymentEntity.getCanceledAmount() : 0L;

        //이번에 취소된 금액 추가
        long newCanceledAmount = currentCanceledAmount + canceledAmount;

        //상태 업데이트
        boolean isPartial = newCanceledAmount < paymentEntity.getAmount();
        paymentEntity.setCanceledAmount(newCanceledAmount);
        paymentEntity.setStatus(isPartial ? Status.PARTIAL_CANCEL : Status.CANCEL);

        paymentRepository.saveAndFlush(paymentEntity);
    }

    private void syncPaymentStatusFromToss(String paymentKey) {
        String authorization = "Basic " + Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes());

        try {
            PaymentSuccessResponseDto tossResponse = tossWebClient.get()
                    .uri(tossPaymentConfig.getConfirmUrl() + paymentKey)
                    .header("Authorization", authorization)
                    .retrieve()
                    .bodyToMono(PaymentSuccessResponseDto.class)
                    .block();

            if (tossResponse != null) {
                PaymentEntity payment = paymentRepository.findByPaymentKey(paymentKey)
                        .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));
                payment.setStatus(Status.fromTossStatus(TossPaymentStatus.valueOf(tossResponse.status().toUpperCase())));
                paymentRepository.save(payment);
            }
        } catch (Exception ex) {
            log.error("보상 트랜잭션 실패 (PG 상태 조회 실패): {}", ex.getMessage());
            //TODO: 별도처리
        }
    }

    private long calculateRefundableAmount(PaymentEntity payment) {
        long totalAmount = payment.getAmount();
        long canceledSoFar = payment.getCanceledAmount() != null ?
                payment.getCanceledAmount() : 0L;
        return totalAmount - canceledSoFar;
    }
}
