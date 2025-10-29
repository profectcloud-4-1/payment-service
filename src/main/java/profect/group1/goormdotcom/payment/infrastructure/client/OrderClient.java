package profect.group1.goormdotcom.payment.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentFailResultDto;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentSuccessResultDto;

import java.util.UUID;

@FeignClient(
        name = "payment-to-order",
        fallback = OrderClientFallBack.class
)
public interface OrderClient {
    @PostMapping("/api/v1/orders/{orderId}/payment/success")
    void notifyPaymentSuccessResult(
            @PathVariable UUID orderId,
            @RequestBody PaymentSuccessResultDto paymentSuccessResultDto
    );

    @PostMapping("/api/v1/orders/{orderId}/payment/fail")
    void notifyPaymentFailResult(
            @PathVariable UUID orderId,
            @RequestBody PaymentFailResultDto paymentFailResultDto
    );
}