package profect.group1.goormdotcom.payment.controller.external.v1.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.enums.PayType;

import java.util.UUID;

@Getter
@Builder
public class PaymentCancelRequestDto {
    private UUID orderId;
    private String reason;
}
