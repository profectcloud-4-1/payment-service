package profect.group1.goormdotcom.user.application;

import profect.group1.goormdotcom.user.domain.User;
import java.util.Optional;
import java.util.List;

public interface UserRepository {
    Optional<User> findById(String id);
    User register(User user, String encryptedPassword);
    User update(User user);
    Optional<User> findByEmail(String email);
    Optional<String> getEncryptedPasswordByEmail(String email);
    List<User> findAll(String sort, String order, String searchField, String keyword, int page, int size, List<List<String>> filter);
    void softDelete(String id, String actor);
    void approveSeller(String userId, String actor, boolean approval);
    void updateNameAndEmail(String userId, String name, String email, String actor);
    void updateLastLoginAt(String userId);
}