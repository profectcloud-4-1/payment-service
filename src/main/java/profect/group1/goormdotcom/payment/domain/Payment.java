package profect.group1.goormdotcom.payment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import profect.group1.goormdotcom.common.domain.BaseEntity;
import profect.group1.goormdotcom.payment.domain.enums.PayType;
import profect.group1.goormdotcom.payment.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    private UUID id;
    private UUID orderId;
    private String orderNumber;
    private String orderName;
    private PayType payType;
    private Status status;
    private Long amount;
    private String paymentKey;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;

    public Payment(
            UUID orderId,
            String orderNumber,
            String orderName,
            PayType payType,
            Long amount
    ) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderName = orderName;
        this.payType = payType;
        this.amount = amount;
        this.status = Status.PENDING;
    }

    public static Payment create(UUID orderId,
                                 String orderNumber,
                                 String orderName,
                                 PayType payType,
                                 Long amount) {
        return new Payment(orderId, orderNumber, orderName, payType, amount);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void markSuccess(String paymentKey, LocalDateTime approvedAt) {
        this.status = Status.SUCCESS;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
    }

    public void markCancel(LocalDateTime cancelledAt) {
        this.status = Status.CANCEL;
        this.cancelledAt = cancelledAt;
    }

    public void markFail() {
        this.status = Status.FAIL;
    }
}
