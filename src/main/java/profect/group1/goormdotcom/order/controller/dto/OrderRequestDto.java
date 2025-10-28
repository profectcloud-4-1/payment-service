package profect.group1.goormdotcom.order.controller.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderRequestDto {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID customerAddressId;

    private String orderName;

    @Min(0)
    private int totalAmount;

    // @Min(1)
    // private int quantity; //테스트용 주문 수량량

    @Valid
    @NotEmpty
    private List<OrderItemDto> items;
}