package profect.group1.goormdotcom.payment.controller.dto.request;

import lombok.Builder;
import lombok.Getter;
import profect.group1.goormdotcom.payment.domain.enums.PayType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentCreateRequestDto {
    private UUID orderId;
    private String orderNumber;
    private String orderName;
    private PayType payType;
    private Long amount;
}
