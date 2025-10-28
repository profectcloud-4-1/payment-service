package profect.group1.goormdotcom.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * 재고 서비스와 통신하는 Feign Client
 * - 재고 확인
 * - 재고 차감
 * - 재고 복구
 */
@FeignClient(name = "stock-service")
public interface StockClient {

    /**
     * 재고 확인
     * @param productId 상품 ID
     * @param quantity 필요 수량
     * @return 재고 충분 여부 (true: 충분, false: 부족)
     */
    @GetMapping("/api/v1/stock/check/{productId}")
    Boolean checkStock(@PathVariable("productId") UUID productId, 
                       @RequestParam("quantity") int quantity);

    /**
     * 재고 차감 (주문 확정 시)
     * @param productId 상품 ID
     * @param quantity 차감할 수량
     * @return 차감 결과
     */
    @PostMapping("/api/v1/stock/decrease/{productId}")
    StockAdjustmentResponseDto decreaseStock(@PathVariable("productId") UUID productId,
                          @RequestParam("quantity") int quantity);

    /**
     * 재고 복구 (주문 취소 시)
     * @param productId 상품 ID
     * @param quantity 복구할 수량
     * @return 복구 결과
     */
    @PostMapping("/api/v1/stock/increase/{productId}")
    StockAdjustmentResponseDto increaseStock(@PathVariable("productId") UUID productId,
                          @RequestParam("quantity") int quantity);
}