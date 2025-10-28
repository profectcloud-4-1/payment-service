package profect.group1.goormdotcom.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * 결제 서비스와 통신하는 Feign Client
 * - 결제 정보 조회
 * - 결제 취소
 */
@FeignClient(name = "payment-service")
public interface PaymentClient {

    /**
     * 결제 정보 조회 (프론트에서 결제 완료 후 호출)
     * @param request 결제 확인 요청
     * @return 결제 성공 여부
     */
    @PostMapping("/api/v1/payment/verify")
    Boolean verifyPayment(@RequestBody PaymentVerifyRequest request);

    /**
     * 결제 취소 (주문 취소 시)
     * @param request 결제 취소 요청
     * @return 취소 성공 여부
     */
    @PostMapping("/api/v1/payments/toss/cancel")
    Boolean cancelPayment(@RequestBody PaymentCancelRequest request);

    /**
     * 결제 확인 요청 DTO
     */
    record PaymentVerifyRequest(
        UUID orderId,
        // UUID paymentId,
        String orderName,
        int amount
    ) {}

    /**
     * 결제 취소 요청 DTO
     */
    record PaymentCancelRequest(
        UUID orderId,
        // UUID paymentId,
        String orderName,
        String reason
    ) {}
}