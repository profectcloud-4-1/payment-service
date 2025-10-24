package profect.group1.goormdotcom.order.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Builder

public class Order {
    private UUID id;
    private UUID customerId;
    private UUID sellerId;
    private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;
    // private LocalDateTime orderDate;
    private int totalAmount;
    // private OrderStatus orderStatus;
    private String orderName;

}