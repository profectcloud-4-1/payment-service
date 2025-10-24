package profect.group1.goormdotcom.product.infrastructure.client.dto;

import java.util.UUID;

public record StockRequestDto(
    UUID productId,
    int stockQuantity
) {
    
}
