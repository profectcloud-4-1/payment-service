package profect.group1.goormdotcom.order.controller.external.v1.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(name = "OrderItem", description = "주문 항목")
public class OrderItemDto {

    @NotNull
    @Schema(description = "상품 ID", example = "123e4567-e89b-12d3-a456-426614174003")
    private UUID productId;

    @NotNull
    // @Min(1)
    @Schema(description = "주문 수량", example = "2")
    private int quantity;
}
