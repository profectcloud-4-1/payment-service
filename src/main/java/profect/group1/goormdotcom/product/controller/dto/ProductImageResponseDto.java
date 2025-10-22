package profect.group1.goormdotcom.product.controller.dto;

import java.util.UUID;

public record ProductImageResponseDto(
    UUID image_id,
    String presiginedUrl
) {
} 
