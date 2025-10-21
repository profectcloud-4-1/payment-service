package profect.group1.goormdotcom.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.createdBy = :actor, u.updatedBy = :actor WHERE u.id = :userId")
    void updateCreatedByAndUpdatedBy(@Param("userId") UUID userId, @Param("actor") String actor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.deletedAt = CURRENT_TIMESTAMP, u.deletedBy = :actor, u.updatedAt = CURRENT_TIMESTAMP, u.updatedBy = :actor WHERE u.id = :userId")
    void softDelete(@Param("userId") UUID userId, @Param("actor") String actor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.name = :name, u.email = :email, u.updatedAt = CURRENT_TIMESTAMP, u.updatedBy = :actor WHERE u.id = :userId")
    void updateNameAndEmail(@Param("userId") UUID userId, @Param("name") String name, @Param("email") String email, @Param("actor") String actor);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE UserJpaEntity u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") UUID userId);
}