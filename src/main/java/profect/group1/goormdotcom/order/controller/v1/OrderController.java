package profect.group1.goormdotcom.order.controller.v1;

import java.util.UUID;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import profect.group1.goormdotcom.order.controller.dto.OrderRequestDto;
import profect.group1.goormdotcom.order.controller.dto.OrderResponseDto;
import profect.group1.goormdotcom.order.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    //주문생성 (재고 확인까지)
    // *POST /api/v1/orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto req) {
        return ResponseEntity.ok(orderService.create(req));
    }
    // 결제 완료 처리(프론트에서 결제 완료 후 호출)
    // POST /api/v1/orders/{orderId}/payment
    // @PostMapping("/{orderId}/payment")
    // public ResponseEntity<OrderResponseDto> completePayment(
    //         @PathVariable UUID orderId,
    //         @RequestParam UUID paymentId) {
    //     return ResponseEntity.ok(orderService.completePayment(orderId, paymentId));
    // }
     /**
     * 배송 상태 업데이트 (배송 서비스에서 호출 or 조회)
     * PUT /api/v1/orders/{orderId}/delivery/status
     */
    @PutMapping("/{orderId}/delivery/status")
    public ResponseEntity<OrderResponseDto> updateDeliveryStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.updateDeliveryStatus(orderId));
    }
    /**
     * 주문 취소
     * POST /api/v1/orders/{orderId}/cancel
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancel(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancel(orderId));
    }
    /**
     * 반송 완료 처리 (배송 서비스에서 webhook 호출)
     * POST /api/v1/orders/{orderId}/return/complete
     */
    // @PostMapping("/{orderId}/return/complete")
    // public ResponseEntity<OrderResponseDto> completeReturn(@PathVariable UUID orderId) {
    //     return ResponseEntity.ok(orderService.completeReturn(orderId));
    // }
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOne(@PathVariable UUID id){
        return ResponseEntity.ok(orderService.getOne(id));
    }
}
