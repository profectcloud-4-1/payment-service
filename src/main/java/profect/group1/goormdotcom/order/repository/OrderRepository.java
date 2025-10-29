package profect.group1.goormdotcom.order.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import profect.group1.goormdotcom.order.repository.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    // OrderEntity getOrderEntityById(UUID Id);

    @Query("SELECT op.order FROM OrderProductEntity op WHERE op.order.customerId = :customerId AND op.productId = :productId")
    Optional<OrderEntity> findByCustomerIdAndProductId(@Param("customerId") UUID customerId,@Param("productId") UUID productId);


}