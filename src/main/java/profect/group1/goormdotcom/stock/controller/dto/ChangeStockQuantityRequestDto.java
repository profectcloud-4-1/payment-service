package profect.group1.goormdotcom.stock.controller.dto;

import java.util.UUID;

public record ChangeStockQuantityRequestDto(
    UUID productId,
    int requestedStockQuantity
) {
}
