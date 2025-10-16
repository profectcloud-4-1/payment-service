package profect.group1.goormdotcom.cart.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDto {

    @NotNull(message = "장바구니 아이템 ID는 필수입니다")
    private UUID cartItemId;

    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 양수여야 합니다")
    private Integer quantity;
}
