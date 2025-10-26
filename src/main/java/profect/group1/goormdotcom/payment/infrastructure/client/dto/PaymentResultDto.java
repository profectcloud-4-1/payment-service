package profect.group1.goormdotcom.payment.infrastructure.client.dto;

import profect.group1.goormdotcom.payment.domain.enums.Status;

import java.time.OffsetDateTime;

public record PaymentResultDto(
        Status status,
        Long amount,
        OffsetDateTime approvedAt
) {};