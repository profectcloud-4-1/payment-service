package profect.group1.goormdotcom.cart.controller.v1;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.apiPayload.code.status.SuccessStatus;
import profect.group1.goormdotcom.cart.controller.dto.CartResponseDto;
import profect.group1.goormdotcom.cart.controller.dto.request.AddCartItemRequestDto;
import profect.group1.goormdotcom.cart.controller.dto.request.DeleteBulkCartItemRequestDto;
import profect.group1.goormdotcom.cart.controller.dto.request.UpdateCartItemRequestDto;
import profect.group1.goormdotcom.cart.controller.mapper.CartDtoMapper;
import profect.group1.goormdotcom.cart.domain.Cart;
import profect.group1.goormdotcom.cart.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CartController implements CartApiDocs {

	private final CartService cartService;

	@GetMapping
	public ApiResponse<CartResponseDto> getCart() {
		Cart cart = cartService.getCart(null);

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<UUID> createCart() {
		UUID cartId = cartService.createCart(null);

		return ApiResponse.of(SuccessStatus._OK, cartId);
	}

	@PostMapping("/items")
	public ApiResponse<CartResponseDto> addItemToCart(
			@RequestBody @Valid AddCartItemRequestDto request
	) {
		Cart cart = cartService.addCartItem(null,
				request.getProductId(),
				request.getQuantity(),
				request.getPrice()
		);

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}

	@PutMapping("/items/{cartItemId}")
	public ApiResponse<CartResponseDto> updateItemToCart(
			@PathVariable(value = "cartItemId") UUID cartItemId,
			@RequestBody @Valid UpdateCartItemRequestDto request
	) {
		Cart cart = cartService.updateCartItem(
				null,
				cartItemId,
				request.getQuantity()
		);

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}

	@PutMapping("/items/bulk-delete")
	public ApiResponse<CartResponseDto> deleteItemFromCart(
			@RequestBody @Valid DeleteBulkCartItemRequestDto request
	) {
		Cart cart = cartService.deleteBulkItem(null, request.getCartItemIds());

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}

	@DeleteMapping("/items/{cartItemId}")
	public ApiResponse<CartResponseDto> deleteItemFromCart(
			@PathVariable(value = "cartItemId") UUID cartItemId
	) {
		Cart cart = cartService.deleteCartItem(null, cartItemId);

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}

	@DeleteMapping("/items")
	public ApiResponse<CartResponseDto> deleteAllItemsFromCart() {
		Cart cart = cartService.clearCart(null);

		return ApiResponse.of(SuccessStatus._OK, CartDtoMapper.toCartDto(cart));
	}
}
