package profect.group1.goormdotcom.cart.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

	private UUID id;
	private UUID customerId;
	private int totalQuantity;
	private int totalPrice;
	private List<CartItem> items = new ArrayList<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public void addItem(final CartItem item) {
		Optional<CartItem> existingItem = items.stream()
				.filter(i -> i.getProductId().equals(item.getProductId()))
				.findFirst();

		if (existingItem.isPresent()) {
			CartItem existingCartItem = existingItem.get();

			existingCartItem.addQuantity(item.getQuantity());
		} else {
			items.add(item);
		}

		calculateTotal();
	}

	public void updateItem(final UUID cartItemId, final int quantity) {
		Optional<CartItem> existingItem = items.stream()
				.filter(i -> i.getId().equals(cartItemId))
				.findFirst();

		if (existingItem.isPresent()) {
			CartItem existingCartItem = existingItem.get();

			existingCartItem.updateQuantity(quantity);
			calculateTotal();
		}
	}

	public void deleteItem(final CartItem item) {
		items.removeIf(i -> i.getProductId().equals(item.getProductId()));
		calculateTotal();
	}

	public void deleteBulkItem(final List<CartItem> items) {
		items.forEach(this::deleteItem);
		calculateTotal();
	}

	public void clear() {
		items.clear();
		calculateTotal();
	}

	public void calculateTotal() {
		this.totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
		this.totalPrice = items.stream()
				.mapToInt(item -> item.getPrice() * item.getQuantity())
				.sum();
	}
}
