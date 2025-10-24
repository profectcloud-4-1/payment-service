package profect.group1.goormdotcom.payment.controller.dto.request;

import lombok.Builder;
import lombok.Getter;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.enums.PayType;

import java.util.UUID;

@Getter
@Builder
public class PaymentCancelRequestDto {
    private String cancelReason;
    //전액 취소면 필요 X
    private Long cancelAmount;
}
