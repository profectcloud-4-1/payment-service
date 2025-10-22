package profect.group1.goormdotcom.payment.controller.dto.response;

import profect.group1.goormdotcom.payment.domain.enums.PayType;
import profect.group1.goormdotcom.payment.domain.enums.Status;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record PaymentSuccessResponseDto (
        String mId,
        String paymentKey,
        PayType payType,
        String orderId,
        String orderName,
        String status,
        OffsetDateTime requestedAt,
        OffsetDateTime approvedAt,
        String currency,
        String totalAmount,
        String balanceAmount,
        String suppliedAmount,
        String method
) {

}