package profect.group1.goormdotcom.delivery.domain.enums;

public enum DeliveryStatus {
    PENDING("DLV0000", "발송 대기"),
    IN_DELIVERY("DLV0001", "배송 중"),
    FINISH("DLV0002", "배송 완료"),
    CANCELLED("DLV0003", "배송 취소");

    private final String code;
    private final String visibleLabel;

    DeliveryStatus(String code, String visibleLabel) {
        this.code = code;
        this.visibleLabel = visibleLabel;
    }

    public String getCode() {
        return code;
    }
    
    public String getVisibleLabel() {
        return visibleLabel;
    }
}