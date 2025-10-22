package profect.group1.goormdotcom.payment.controller.dto.response;

import profect.group1.goormdotcom.payment.domain.enums.PayType;
import profect.group1.goormdotcom.payment.domain.enums.Status;

import java.util.UUID;


public record PaymentResponseDto (
        UUID id,
        PayType payType,
        Status status,
        Long amount
) {

}
