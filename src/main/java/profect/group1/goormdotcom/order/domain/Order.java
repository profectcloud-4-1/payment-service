package profect.group1.goormdotcom.order.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import profect.group1.goormdotcom.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(name = "Order", description = "주문 정보")
public class Order {
    @Schema(description = "주문 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    @Schema(description = "고객 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID customerId;
    @Schema(description = "생성 시각", example = "2025-01-01T12:34:56")
    private LocalDateTime createdAt;
	@Schema(description = "수정 시각", example = "2025-01-02T09:10:11")
	private LocalDateTime updatedAt;
	@Schema(description = "삭제 시각", example = "2025-01-03T00:00:00")
	private LocalDateTime deletedAt;
    // private LocalDateTime orderDate;
    @Schema(description = "총 결제 금액(원)", example = "35000")
    private int totalAmount;
    @Schema(description = "주문 상태", implementation = OrderStatus.class, example = "PENDING")
    private OrderStatus status; // "ORD0001" 등 문자열
    // private OrderStatus orderStatus;
    @Schema(description = "주문명", example = "양말 외 2종")
    private String orderName;

}