package profect.group1.goormdotcom.order.controller.external.v1;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.order.controller.external.v1.dto.OrderRequestDto;
import profect.group1.goormdotcom.order.domain.Order;
import profect.group1.goormdotcom.user.controller.auth.LoginUser;

@Tag(name = "Order", description = "주문 API")
public interface OrderApiDocs {

	@Operation(summary = "주문 생성", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<Order> create(@RequestBody OrderRequestDto req, @LoginUser UUID userId);

	@Operation(summary = "배송 전 주문 취소", description = "배송 전 주문 취소 및 환불", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<Order> deliveryBefore(@PathVariable UUID orderId);

	@Operation(summary = "배송 후 주문 취소", description = "배송 후 주문 취소 및 반품", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<Order> cancel(@PathVariable UUID orderId);

	@Operation(summary = "주문 전체 조회", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<List<Order>> getAllOrders();

	@Operation(summary = "주문 단건 조회", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<Order> getOne(@PathVariable UUID id);
}
