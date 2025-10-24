package profect.group1.goormdotcom.payment.controller.dto.request;

import java.util.UUID;

public record PaymentCanceledEvent(
        UUID paymentId,
        String paymentKey,
        PaymentCancelRequestDto requestDto
) {}
