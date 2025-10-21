package profect.group1.goormdotcom.user.infra;

import org.springframework.stereotype.Repository;
import profect.group1.goormdotcom.user.application.UserRepository;
import profect.group1.goormdotcom.user.domain.User;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import java.util.Optional;
import profect.group1.goormdotcom.user.infra.UserJpaRepository;
import profect.group1.goormdotcom.user.infra.converter.UserJpaConverter;
import profect.group1.goormdotcom.user.infra.UserJpaEntity;
import profect.group1.goormdotcom.user.domain.enums.SellerApprovalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepo;
    @PersistenceContext
    private EntityManager entityManager;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "createdAt", "name"
    );

    private static final Set<String> ALLOWED_SEARCH_FIELDS = Set.of(
        "email", "name"
    );

    @Override
    public Optional<User> findById(String id) {
        UUID uuid = UUID.fromString(id);
        User user = jpaRepo.findById(uuid).map(UserJpaConverter::toDomain).orElse(null);
        return Optional.ofNullable(user);
    }

    @Override
    public User register(User user, String encryptedPassword) {
        UserJpaEntity entity = UserJpaConverter.toEntity(user);
        entity.setPassword(encryptedPassword);
        User registeredUser = UserJpaConverter.toDomain(jpaRepo.save(entity));

        // user의 createdBy와 updatedBy를 registeredUser.id로 update
        jpaRepo.updateCreatedByAndUpdatedBy(registeredUser.getId(), registeredUser.getId().toString());

        return registeredUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepo.findByEmail(email).map(UserJpaConverter::toDomain);
    }

    @Override
    public User update(User user) {
        UserJpaEntity entity = UserJpaConverter.toEntity(user);
        return UserJpaConverter.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<String> getEncryptedPasswordByEmail(String email) {
        return jpaRepo.findByEmail(email).map(UserJpaEntity::getPassword);
    }

    @Override
    public List<User> findAll(String sort, String order, String searchField, String keyword, int page, int size, List<List<String>> filter) {
        String sortField = (sort != null && ALLOWED_SORT_FIELDS.contains(sort)) ? sort : "createdAt";
        boolean isAsc = "asc".equalsIgnoreCase(order);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserJpaEntity> cq = cb.createQuery(UserJpaEntity.class);
        Root<UserJpaEntity> root = cq.from(UserJpaEntity.class);

        Predicate where = cb.conjunction();
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasSearchField = searchField != null && ALLOWED_SEARCH_FIELDS.contains(searchField);

        if (hasKeyword && hasSearchField) {
            where = cb.and(where, cb.like(cb.lower(root.get(searchField).as(String.class)), "%" + keyword.toLowerCase() + "%"));
        }
        if (filter != null && !filter.isEmpty()) {
            for (List<String> filterItem : filter) {
                String filterField = filterItem.get(0);
                String filterValue = filterItem.get(1);
                where = cb.and(where, cb.equal(root.get(filterField).as(String.class), filterValue));
            }
        }
        cq.where(where);

        cq.orderBy(
            isAsc? 
            cb.asc(root.get(sortField)) : 
            cb.desc(root.get(sortField))
        );
        

        var query = entityManager.createQuery(cq);
        int safePage = Math.max(1, page);
        int safeSize = (size == 10 || size == 30 || size == 50) ? size : 10;
        query.setFirstResult((safePage - 1) * safeSize);
        query.setMaxResults(safeSize);

        List<UserJpaEntity> entities = query.getResultList();
        return entities.stream().map(UserJpaConverter::toDomain).toList();
    }

    @Override
    public void softDelete(String id, String actor) {
        UUID uuid = UUID.fromString(id);
        jpaRepo.softDelete(uuid, actor);
    }

    @Override
    public void approveSeller(String userId, String actor, boolean approval) {
        UUID uuid = UUID.fromString(userId);
        UserJpaEntity entity = jpaRepo.findById(uuid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        entity.setSellerApprovalStatus(approval ? SellerApprovalStatus.APPROVED : SellerApprovalStatus.REJECTED);
        entity.setUpdatedBy(actor);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setApprovedAt(LocalDateTime.now());
        entity.setApprovedBy(actor);
        jpaRepo.save(entity);
    }

    @Override
    public void updateNameAndEmail(String userId, String name, String email, String actor) {
        if (name==null && email==null) return;
        
        UUID uuid = UUID.fromString(userId);
        UserJpaEntity entity = jpaRepo.findById(uuid).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (name != null) entity.setName(name);
        if (email != null) entity.setEmail(email);

        entity.setUpdatedBy(actor);
        jpaRepo.save(entity);
    }

    @Override
    public void updateLastLoginAt(String userId) {
        UUID uuid = UUID.fromString(userId);
        jpaRepo.updateLastLoginAt(uuid);
    }
}