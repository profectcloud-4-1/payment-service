package profect.group1.goormdotcom.payment.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentResultDto;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        url = "${order.service.url}",
        fallback = OrderClientFallBack.class
)
public interface OrderClient {
    //TODO: order에서 실제 컨트롤러 url 맞춰서 바꾸기
    @PostMapping("/internal/orders/{orderId}/payment-result")
    void notifyPaymentResult(@PathVariable UUID orderId, @RequestBody PaymentResultDto dto);
}