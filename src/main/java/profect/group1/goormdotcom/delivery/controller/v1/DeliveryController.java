package profect.group1.goormdotcom.delivery.controller.v1;

import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.delivery.service.DeliveryService;
import profect.group1.goormdotcom.delivery.domain.Delivery;
import profect.group1.goormdotcom.delivery.domain.DeliveryReturn;
import profect.group1.goormdotcom.delivery.domain.DeliveryAddress;
import profect.group1.goormdotcom.delivery.controller.dto.response.CustomerAddressListResponseDto;
import profect.group1.goormdotcom.delivery.controller.dto.request.CreateAddressRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import profect.group1.goormdotcom.delivery.controller.dto.request.CreateDeliveryRequestDto;
import profect.group1.goormdotcom.delivery.controller.dto.request.CancelDeliveryRequestDto;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryController implements DeliveryApiDocs {

	private final DeliveryService service;

	@GetMapping("/check/cancellable")
	public ApiResponse<Integer> checkCancellable(@RequestParam UUID orderId) {
		Integer canReturn = this.service.canReturn(orderId);
		return ApiResponse.onSuccess(canReturn);
	}

    @PostMapping
	public ApiResponse<Delivery> createDelivery(@RequestBody @Valid CreateDeliveryRequestDto body) {
		Delivery delivery = this.service.createDelivery(body.getOrderId(), body.getCustomerAddressId());
		return ApiResponse.onSuccess(delivery);
	}

    @PostMapping("/cancel")
	public ApiResponse<Object> cancelDelivery(@RequestBody @Valid CancelDeliveryRequestDto body) {
		try {
			this.service.cancel(body.getOrderId());
			return ApiResponse.onSuccess(null);
		} catch (Exception e) {
			String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
			String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
			switch (e.getMessage()) {
				case "Delivery not found":
					code = ErrorStatus._NOT_FOUND.getCode();
					message = "배송 정보를 찾을 수 없습니다.";
					break;
				case "Delivery cannot be cancelled":
					code = ErrorStatus._FORBIDDEN.getCode();
					message = "현재 취소 가능한 상태가 아닙니다.";
					break;
			}
			return ApiResponse.onFailure(String.valueOf(code), message, null);
		}
	}

    @PostMapping("/return")
	public ApiResponse<Object> returnDelivery(@RequestBody @Valid CancelDeliveryRequestDto body) {
		try {
			this.service.returnDelivery(body.getOrderId());
			return ApiResponse.onSuccess(null);
		} catch (Exception e) {
			String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
			String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
			switch (e.getMessage()) {
				case "Delivery not found":
					code = ErrorStatus._NOT_FOUND.getCode();
					message = "배송 정보를 찾을 수 없습니다.";
					break;
				case "Delivery cannot be returned":
					code = ErrorStatus._FORBIDDEN.getCode();
					message = "현재 반송 가능한 상태가 아닙니다.";
					break;
			}
			return ApiResponse.onFailure(String.valueOf(code), message, null);
		}
	}

	@GetMapping("/address/mine")
	public ApiResponse<CustomerAddressListResponseDto> getMyAddresses(
		HttpServletRequest request
		) {
		Claims claims = (Claims) request.getAttribute("jwtClaims");
		UUID customerId = UUID.fromString(claims.getSubject());
		List<DeliveryAddress> addresses = this.service.getAddressesByCustomerId(customerId);
		return ApiResponse.onSuccess(CustomerAddressListResponseDto.of(addresses));
	}

	@PostMapping("/address/mine")
	public ApiResponse<DeliveryAddress> createMyAddress(
		@RequestBody @Valid CreateAddressRequestDto body,
		HttpServletRequest request
	) {
		Claims claims = (Claims) request.getAttribute("jwtClaims");
		UUID customerId = UUID.fromString(claims.getSubject());
		DeliveryAddress address = this.service.createCustomerAddress(customerId, body);
		return ApiResponse.onSuccess(address);
	}

    @PutMapping("/address/mine/{addressId}")
	public ApiResponse<DeliveryAddress> updateMyAddress(
		@RequestBody @Valid CreateAddressRequestDto body,
        HttpServletRequest request,
        @PathVariable UUID addressId
	) {
		Claims claims = (Claims) request.getAttribute("jwtClaims");
		UUID customerId = UUID.fromString(claims.getSubject());
        DeliveryAddress address = this.service.updateCustomerAddress(customerId, addressId, body);
		return ApiResponse.onSuccess(address);
	}

    @DeleteMapping("/address/mine/{addressId}")
	public ApiResponse<Boolean> deleteMyAddress(
        HttpServletRequest request,
        @PathVariable UUID addressId
	) {
		Claims claims = (Claims) request.getAttribute("jwtClaims");
		UUID customerId = UUID.fromString(claims.getSubject());
        boolean ok = this.service.deleteCustomerAddress(customerId, addressId);
		return ApiResponse.onSuccess(ok);
	}

	// ===== Goorm Address (MASTER 전용) =====
    @GetMapping("/address/goorm")
	@PreAuthorize("hasRole('MASTER')")
    public ApiResponse<DeliveryAddress> getGoormAddress() {
		try {
        return ApiResponse.onSuccess(this.service.getGoormAddress());
		} catch (Exception e) {
			String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
            String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
            switch (e.getMessage()) {
                case "Goorm address not found":
                    code = ErrorStatus._NOT_FOUND.getCode();
                    message = "등록된 배송지가 없습니다.";
                    break;
            }
            return ApiResponse.onFailure(String.valueOf(code), message, null);
		}
	}

	@PostMapping("/address/goorm")
	@PreAuthorize("hasRole('MASTER')")
    public ApiResponse<DeliveryAddress> createGoormAddress(
        @RequestBody @Valid CreateAddressRequestDto body
    ) {
		try {
        DeliveryAddress address = this.service.createGoormAddress(body);
		return ApiResponse.onSuccess(address);
		} catch (Exception e) {
			String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
            String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
            switch (e.getMessage()) {
                case "Goorm address already exists":
                    code = ErrorStatus._CONFLICT.getCode();
                    message = ErrorStatus._CONFLICT.getMessage();
                    break;
            }
            return ApiResponse.onFailure(String.valueOf(code), message, null);
		}
	}

	@PutMapping("/address/goorm")
	@PreAuthorize("hasRole('MASTER')")
	public ApiResponse<DeliveryAddress> updateGoormAddress(
		@RequestBody @Valid CreateAddressRequestDto body
	) {
		DeliveryAddress address = this.service.updateGoormAddress(body);
		return ApiResponse.onSuccess(address);
	}
}
