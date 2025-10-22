package profect.group1.goormdotcom.payment.controller.mapper;

import org.springframework.stereotype.Component;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;

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