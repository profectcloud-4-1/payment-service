package profect.group1.goormdotcom.payment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import profect.group1.goormdotcom.payment.domain.enums.Status;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByOrderIdAndStatus(UUID orderId, Status status);

    Optional<PaymentEntity> findByOrderNumber(String orderNumber);

    Optional<PaymentEntity> findByPaymentKey(String paymentKey);
}
