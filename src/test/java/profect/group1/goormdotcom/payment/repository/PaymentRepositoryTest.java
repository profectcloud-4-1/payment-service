package profect.group1.goormdotcom.payment.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("PaymentRepository 테스트")
public class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentEntity createAndPersistPaymentEntity(UUID orderId, String paymentKey, Long amount, String status) {
        PaymentEntity payment = new PaymentEntity(
                null,
                UUID.randomUUID(),
                orderId,
                "Test Order",
                status,
                amount,
                0L,
                paymentKey,
                LocalDateTime.now(),
                null
        );
        return entityManager.persist(payment);
    }

    @Nested
    @DisplayName("결제 생성 및 업데이트 테스트")
    class SaveTest {

        @Test
        @DisplayName("성공 - 새로운 결제를 저장하면 INSERT가 실행된다.")
        void save_NewEntity_PerformsInsert() {
            // given
            UUID orderId = UUID.randomUUID();
            String paymentKey = UUID.randomUUID().toString();
            PaymentEntity newPayment = new PaymentEntity(
                    null, // id is generated
                    UUID.randomUUID(),
                    orderId,
                    "New Order",
                    "PAY0000",
                    15000L,
                    0L,
                    paymentKey,
                    null,
                    null
            );

            // when
            PaymentEntity savedPayment = paymentRepository.save(newPayment);
            entityManager.flush();
            entityManager.clear();

            // then
            assertThat(savedPayment.getId()).isNotNull();
            PaymentEntity foundInDb = entityManager.find(PaymentEntity.class, savedPayment.getId());
            assertThat(foundInDb).isNotNull();
            assertThat(foundInDb.getOrderId()).isEqualTo(orderId);
            assertThat(foundInDb.getPaymentKey()).isEqualTo(paymentKey);
            assertThat(foundInDb.getAmount()).isEqualTo(15000L);
            assertThat(foundInDb.getStatus()).isEqualTo("PAY0000");
        }

        @Test
        @DisplayName("성공 - 기존 결제를 저장하면 UPDATE가 실행된다.")
        void save_ExistingEntity_PerformsUpdate() {
            // given
            PaymentEntity originalPayment = createAndPersistPaymentEntity(UUID.randomUUID(), UUID.randomUUID().toString(), 10000L, "PAY0000");
            entityManager.flush();
            entityManager.clear();

            // when
            PaymentEntity paymentToUpdate = entityManager.find(PaymentEntity.class, originalPayment.getId());
            paymentToUpdate.setStatus("PAY0001");
            paymentToUpdate.setApprovedAt(LocalDateTime.now().plusHours(1));

            paymentRepository.save(paymentToUpdate);
            entityManager.flush();
            entityManager.clear();

            // then
            PaymentEntity foundAfterUpdate = entityManager.find(PaymentEntity.class, originalPayment.getId());
            assertThat(foundAfterUpdate.getStatus()).isEqualTo("PAY0001");
            assertThat(foundAfterUpdate.getApprovedAt()).isNotNull();
            assertThat(foundAfterUpdate.getApprovedAt()).isNotEqualTo(originalPayment.getApprovedAt());
        }
    }

    @Nested
    @DisplayName("결제 조회 테스트")
    class FindTest {

        @Test
        @DisplayName("성공 - ID로 결제를 조회할 수 있다.")
        void findById_Success() {
            // given
            PaymentEntity payment = createAndPersistPaymentEntity(UUID.randomUUID(), UUID.randomUUID().toString(), 20000L, "PAY0001");
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findById(payment.getId());

            // then
            assertThat(foundPayment).isPresent();
            assertThat(foundPayment.get().getId()).isEqualTo(payment.getId());
            assertThat(foundPayment.get().getPaymentKey()).isEqualTo(payment.getPaymentKey());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 조회하면 Optional.empty를 반환한다.")
        void findById_NotFound_ReturnsEmpty() {
            // given
            UUID nonExistentId = UUID.randomUUID();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findById(nonExistentId);

            // then
            assertThat(foundPayment).isNotPresent();
        }

        @Test
        @DisplayName("성공 - orderId로 결제를 조회할 수 있다.")
        void findByOrderId_Success() {
            // given
            UUID orderId = UUID.randomUUID();
            createAndPersistPaymentEntity(orderId, UUID.randomUUID().toString(), 30000L, "PAY0000");
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByOrderId(orderId);

            // then
            assertThat(foundPayment).isPresent();
            assertThat(foundPayment.get().getOrderId()).isEqualTo(orderId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 orderId로 조회하면 Optional.empty를 반환한다.")
        void findByOrderId_NotFound_ReturnsEmpty() {
            // given
            UUID nonExistentOrderId = UUID.randomUUID();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByOrderId(nonExistentOrderId);

            // then
            assertThat(foundPayment).isNotPresent();
        }

        @Test
        @DisplayName("성공 - orderId와 status로 결제를 조회할 수 있다.")
        void findByOrderIdAndStatus_Success() {
            // given
            UUID orderId = UUID.randomUUID();
            String status = "PAY0000";
            createAndPersistPaymentEntity(orderId, UUID.randomUUID().toString(), 40000L, status);
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByOrderIdAndStatus(orderId, status);

            // then
            assertThat(foundPayment).isPresent();
            assertThat(foundPayment.get().getOrderId()).isEqualTo(orderId);
            assertThat(foundPayment.get().getStatus()).isEqualTo(status);
        }

        @Test
        @DisplayName("실패 - orderId와 status가 일치하지 않으면 Optional.empty를 반환한다.")
        void findByOrderIdAndStatus_NotFound_ReturnsEmpty() {
            // given
            UUID orderId = UUID.randomUUID();
            String status = "PAY0000";
            createAndPersistPaymentEntity(orderId, UUID.randomUUID().toString(), 40000L, status);
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByOrderIdAndStatus(orderId, "PAY0001"); // Different status

            // then
            assertThat(foundPayment).isNotPresent();
        }

        @Test
        @DisplayName("성공 - paymentKey로 결제를 조회할 수 있다.")
        void findByPaymentKey_Success() {
            // given
            String paymentKey = UUID.randomUUID().toString();
            createAndPersistPaymentEntity(UUID.randomUUID(), paymentKey, 50000L, "PAY0001");
            entityManager.flush();
            entityManager.clear();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByPaymentKey(paymentKey);

            // then
            assertThat(foundPayment).isPresent();
            assertThat(foundPayment.get().getPaymentKey()).isEqualTo(paymentKey);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 paymentKey로 조회하면 Optional.empty를 반환한다.")
        void findByPaymentKey_NotFound_ReturnsEmpty() {
            // given
            String nonExistentPaymentKey = UUID.randomUUID().toString();

            // when
            Optional<PaymentEntity> foundPayment = paymentRepository.findByPaymentKey(nonExistentPaymentKey);

            // then
            assertThat(foundPayment).isNotPresent();
        }

        @Test
        @DisplayName("성공 - paymentKey 존재 여부를 확인할 수 있다 (true).")
        void existsByPaymentKey_True() {
            // given
            String paymentKey = UUID.randomUUID().toString();
            createAndPersistPaymentEntity(UUID.randomUUID(), paymentKey, 60000L, "PAY0001");
            entityManager.flush();
            entityManager.clear();

            // when
            boolean exists = paymentRepository.existsByPaymentKey(paymentKey);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("성공 - paymentKey 존재 여부를 확인할 수 있다 (false).")
        void existsByPaymentKey_False() {
            // given
            String nonExistentPaymentKey = UUID.randomUUID().toString();

            // when
            boolean exists = paymentRepository.existsByPaymentKey(nonExistentPaymentKey);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("결제 삭제 테스트")
    class DeleteTest {

        @Test
        @DisplayName("성공 - 결제를 삭제하면 findById로 조회되지 않는다.")
        void delete_Success_NotFindable() {
            // given
            PaymentEntity payment = createAndPersistPaymentEntity(UUID.randomUUID(), UUID.randomUUID().toString(), 5000L, "PAY0000");
            entityManager.flush();
            UUID paymentId = payment.getId();

            // when
            paymentRepository.deleteById(paymentId);
            entityManager.flush();
            entityManager.clear();

            // then
            Optional<PaymentEntity> foundPayment = paymentRepository.findById(paymentId);
            assertThat(foundPayment).isNotPresent();
        }
    }
}