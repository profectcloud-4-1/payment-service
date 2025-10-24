package profect.group1.goormdotcom.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * 배송 서비스와 통신하는 Feign Client
 * - 배송 요청
 * - 배송 상태 조회
 * - 배송 취소/반송 요청
 */
@FeignClient(name = "delivery-service", url = "${delivery.service.url}")
public interface DeliveryClient {

    /**
     * 배송 요청 (결제 완료 후)
     * @param request 배송 요청 정보
     * @return 배송 요청 성공 여부
     */
    @PostMapping("/api/v1/delivery/request")
    Boolean requestDelivery(@RequestBody DeliveryRequest request);

    /**
     * 배송 상태 조회
     * @param orderId 주문 ID
     * @return 배송 상태
     */
    @GetMapping("/api/v1/delivery/status/{orderId}")
    DeliveryStatusResponse getDeliveryStatus(@PathVariable("orderId") UUID orderId);

    /**
     * 배송 취소 요청 (배송 시작 전)
     * @param orderId 주문 ID
     * @return 취소 성공 여부
     */
    @PostMapping("/api/v1/delivery/cancel/{orderId}")
    Boolean cancelDelivery(@PathVariable("orderId") UUID orderId);

    /**
     * 반송 요청 (배송 완료 후 취소)
     * @param orderId 주문 ID
     * @return 반송 요청 성공 여부
     */
    @PostMapping("/api/v1/delivery/return/{orderId}")
    Boolean requestReturn(@PathVariable("orderId") UUID orderId);

    /**
     * 배송 요청 DTO
     */
    record DeliveryRequest(
        UUID orderId,
        UUID customerId,
        String address,
        String recipientName,
        String recipientPhone
    ) {}

    /**
     * 배송 상태 응답 DTO
     */
    record DeliveryStatusResponse(
        UUID orderId,
        DeliveryStatus status,
        String trackingNumber
    ) {}

    /**
     * 배송 상태 enum
     */
    enum DeliveryStatus {
        PREPARING,      // 배송 준비 중
        SHIPPED,        // 배송 중
        DELIVERED,      // 배송 완료
        RETURNED,       // 반송 완료
        CANCELLED       // 취소됨
    }
}