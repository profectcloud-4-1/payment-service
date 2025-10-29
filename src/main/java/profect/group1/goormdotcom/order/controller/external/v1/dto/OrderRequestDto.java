package profect.group1.goormdotcom.order.controller.external.v1.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Schema(name = "OrderRequest", description = "주문 생성 요청 바디")
public class OrderRequestDto {

    @Schema(description = "주문명", example = "양말 외 2종")
    private String orderName;

    @Min(0)
    @Schema(description = "총 결제 금액(원)", example = "35000")
    private int totalAmount;

    @Schema(description = "배송지 주소")
    private String address;
    @Schema(description = "배송지 상세주소")
    private String addressDetail;
    @Schema(description = "배송지 우편번호")
    private String zipcode;
    @Schema(description = "배송지 전화번호")
    private String phone;
    @Schema(description = "수취인 이름")
    private String name;
    @Schema(description = "배송 메모")
    private String deliveryMemo;

    // @Min(1)
    // private int quantity; //테스트용 주문 수량량

    @Valid
    @NotEmpty
    @Schema(description = "주문 아이템 목록", required = true)
    private List<OrderItemDto> items;
}