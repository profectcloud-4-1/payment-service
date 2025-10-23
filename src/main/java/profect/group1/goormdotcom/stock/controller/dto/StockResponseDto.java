package profect.group1.goormdotcom.stock.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockResponseDto(
    UUID productId,
    int stockQuantity,
    LocalDateTime updatedAt
) {
}
