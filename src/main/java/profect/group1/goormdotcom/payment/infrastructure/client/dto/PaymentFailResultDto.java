package profect.group1.goormdotcom.payment.infrastructure.client.dto;

import java.time.OffsetDateTime;

public record PaymentFailResultDto(
        String status,
        Long amount
) {};
