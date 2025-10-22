package profect.group1.goormdotcom.payment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.PaymentHandler;
import profect.group1.goormdotcom.payment.config.TossPaymentConfig;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentCreateRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.enums.Status;
import profect.group1.goormdotcom.payment.repository.PaymentRepository;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;
import profect.group1.goormdotcom.payment.repository.mapper.PaymentMapper;
import profect.group1.goormdotcom.user.domain.User;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
    public Payment requestPayment(PaymentCreateRequestDto dto, User user) {
        //TODO: order에서 orderId 존재하는지 확인? MSA에서는 처리할 것인지 고민

        //처리중인 결제내역이 있는지 확인
        if (dto.getOrderId() != null) {
            paymentRepository.findByOrderIdAndStatus(dto.getOrderId(), Status.PENDING)
                    .ifPresent(existing -> {
                        throw new PaymentHandler(ErrorStatus._DUPLICATE_PAYMENT_REQUEST);
                    });
        }

        //TODO: 로직 검증
        String orderNumber = dto.getOrderNumber();
        if (orderNumber == null || orderNumber.isBlank()) {
            orderNumber = generateOrderNumber(); // 아래 유틸 메서드
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

    private String generateOrderNumber() {
        // 예: ORD-20251021-024651-XYZ12 (날짜+시분초+5자리)
        var now = java.time.LocalDateTime.now();
        String ts = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String rand = java.util.UUID.randomUUID().toString().substring(0,5).toUpperCase();
        return "ORD-" + ts + "-" + rand;
    }

    @Transactional
    public PaymentSuccessResponseDto tossPaymentSuccess(PaymentSuccessRequestDto dto) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderNumber(dto.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));
        if (!paymentEntity.getAmount().equals(dto.getAmount())) {
            throw new PaymentHandler(ErrorStatus._INVALID_PAYMENT_AMOUNT);
        }

        WebClient webClient = WebClient.create(tossPaymentConfig.getConfirmUrl());

        String authorizations = "Basic " + Base64.getEncoder().encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes());

        //TODO: RestClient로 바꾸는 것 고려
        PaymentSuccessResponseDto response = webClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
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

        return response;
    }

    @Transactional
    public void tossPaymentFail(PaymentFailRequestDto dto) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderNumber(dto.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        paymentEntity.setStatus(Status.FAIL);
    }

    @Transactional
    public PaymentCancelResponseDto tossPaymentCancel(PaymentCancelRequestDto dto, String paymentKey) {
        PaymentEntity paymentEntity = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new PaymentHandler(ErrorStatus._PAYMENT_NOT_FOUND));

        WebClient webClient = WebClient.create(tossPaymentConfig.getConfirmUrl());

        String authorizations = "Basic " + Base64.getEncoder().encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes());

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", dto.getCancelReason());
        if (dto.getCancelAmount() != null) { //부분 최소
            body.put("cancelAmount", dto.getCancelAmount());
        }

        //TODO: RestClient로 바꾸는 것 고려
        PaymentCancelResponseDto response = webClient.post()
                .uri(tossPaymentConfig.getConfirmUrl() + paymentKey + "/cancel")
                .header("Authorization", authorizations)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PaymentCancelResponseDto.class)
                .block();

        paymentEntity.setStatus(Status.CANCEL);

        //TODO: 히스토리에 취소사유
        return response;
    }

}
