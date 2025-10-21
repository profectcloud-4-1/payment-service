package profect.group1.goormdotcom.payment.controller.mapper;

import org.springframework.stereotype.Component;
import profect.group1.goormdotcom.payment.controller.dto.PaymentResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.domain.enums.PayType;
import profect.group1.goormdotcom.payment.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentDtoMapper {
    public static PaymentResponseDto toPaymentDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getPayType(),
                payment.getStatus(),
                payment.getAmount()
        );
    }
}