package profect.group1.goormdotcom.payment.controller.external.v1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentSuccessRequestDto {
    @NotNull(message = "orderId는 필수입니다.")
    private UUID orderId;

    @NotNull(message = "orderName는 필수입니다.")
    private String orderName;

    @NotNull(message = "paymentKey는 필수입니다.")
    private String paymentKey;

    @NotNull(message = "amount는 필수입니다.")
    private Long amount;
}
