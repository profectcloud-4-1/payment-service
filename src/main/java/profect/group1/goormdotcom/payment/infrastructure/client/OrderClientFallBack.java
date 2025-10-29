package profect.group1.goormdotcom.payment.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentFailResultDto;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentSuccessResultDto;

import java.util.UUID;

@Slf4j
@Component
public class OrderClientFallBack implements OrderClient {

    @Override
    public void notifyPaymentSuccessResult(UUID orderId, PaymentSuccessResultDto dto) {
        log.error("[Feign-Fallback] order-service 호출 실패: orderId={}, dto={}", orderId, dto);
        // TODO: MSA 분리 시 아웃박스 추가
    }

    @Override
    public void notifyPaymentFailResult(UUID orderId, PaymentFailResultDto dto) {
        log.error("[Feign-Fallback] order-service 호출 실패: orderId={}, dto={}", orderId, dto);
        // TODO: MSA 분리 시 아웃박스 추가
    }
}