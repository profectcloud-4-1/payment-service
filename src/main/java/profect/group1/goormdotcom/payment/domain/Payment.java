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
    private UUID userId;
    private UUID orderId;
    private String orderName;
    private String status;
    private Long amount;
    private Long canceledAmount;
    private String paymentKey;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Payment(
            UUID userId,
            UUID orderId,
            String orderName,
            String paymentKey,
            Long amount
    ) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderName = orderName;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.canceledAmount = (long) 0;
        this.status = "PAY0000"; //DB에서 펜딩상태 긁어오기
    }

    public static Payment create(UUID userId,
                                 UUID orderId,
                                 String orderName,
                                 String paymentKey,
                                 Long amount) {
        return new Payment(userId, orderId, orderName, paymentKey, amount);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void markSuccess(String paymentKey, LocalDateTime approvedAt) {
        this.status = "PAY0001"; //DB에서 성공상태 긁어오기
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
    }

    public void markCancel(LocalDateTime canceledAt) {
        this.status = "PAY0004"; //DB에서 취소상태 긁어오기
        this.canceledAt = canceledAt;
    }

    public void markFail() {
        this.status = "PAY0002"; //DB에서 실패상태 긁어오기
    }
}
