package profect.group1.goormdotcom.payment.infrastructure.client.dto;

import java.time.OffsetDateTime;

public record PaymentSuccessResultDto(
        String status,
        Long amount,
        OffsetDateTime approvedAt
) {};