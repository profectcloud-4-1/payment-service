package profect.group1.goormdotcom.cart.repository.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import profect.group1.goormdotcom.cart.domain.Cart;
import profect.group1.goormdotcom.cart.domain.CartItem;
import profect.group1.goormdotcom.cart.repository.entity.CartEntity;

@Component
public class CartMapper {

	public static Cart toDomain(
			final CartEntity entity,
			final List<CartItem> items
	) {
		return new Cart(
				entity.getId(),
				entity.getCustomerId(),
				items,
				entity.getCreatedAt(),
				entity.getUpdatedAt()
		);
	}
}
