package profect.group1.goormdotcom.user.application;

import profect.group1.goormdotcom.user.domain.User;
import profect.group1.goormdotcom.user.domain.enums.SellerApprovalStatus;
import profect.group1.goormdotcom.user.domain.enums.UserRole;
import profect.group1.goormdotcom.user.application.dto.CreateUserDto;
import profect.group1.goormdotcom.user.application.PasswordService;
import profect.group1.goormdotcom.common.security.JwtTokenProvider;
import profect.group1.goormdotcom.user.presentation.dto.request.ListRequestDto;

import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository repo, PasswordService passwordService, JwtTokenProvider jwtTokenProvider) {
        this.repo = repo;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Optional<User> findById(String id) {
        return repo.findById(id);
    }

    public boolean isEmailExists(String email) {
        boolean exists = repo.findByEmail(email).isPresent();
        return exists;
    }

    public User register(CreateUserDto body) {
        if (!passwordService.validate(body.getPassword())) throw new IllegalArgumentException("Invalid password");
        String encryptedPassword = passwordService.encrypt(body.getPassword());

        if (isEmailExists(body.getEmail())) throw new IllegalArgumentException("Email already exists");

        SellerApprovalStatus approval =
            body.getRole() == UserRole.SELLER ? SellerApprovalStatus.PENDING : null;

        User user = User.builder()
            .name(body.getName())
            .email(body.getEmail())
            .role(body.getRole())
            .brandId(body.getBrandId())
            .sellerApprovalStatus(approval)
            .build();

        return repo.register(user, encryptedPassword);
    }

    public String login(String email, String password) {
        var userOpt = repo.findByEmail(email);
        if (userOpt.isEmpty()) throw new IllegalArgumentException("Invalid credentials");
        String encoded = repo.getEncryptedPasswordByEmail(email).orElse("");
        if (!passwordService.isMatch(password, encoded)) throw new IllegalArgumentException("Invalid credentials");

        User user = userOpt.get();
        repo.updateLastLoginAt(user.getId().toString());
        String role = user.getRole() != null ? user.getRole().name() : null;
        return jwtTokenProvider.generateAccessToken(user.getId(), role);
    }

    public void leave(String userId) {
        repo.softDelete(userId, userId);
    }

    public void approve(String userId, String actor, boolean approval) {
        repo.approveSeller(userId, actor, approval);
    }

    public void edit(String userId, String name, String email) {
        User user = repo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (email != null && !email.equals(user.getEmail())) {
            if (isEmailExists(email)) throw new IllegalArgumentException("Email already exists");
        }
        repo.updateNameAndEmail(userId, name, email, userId);
    }

    public void delete(String userId, String actor) {
        repo.softDelete(userId, actor);
    }

    public List<User> findAllBy(ListRequestDto body) {
        List<List<String>> filter = null;
        if (body.getFilter() != null && !body.getFilter().isBlank()) {
            filter = Arrays.stream(body.getFilter().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(pair -> pair.split(":", 2))
                .filter(arr -> arr.length == 2 && !arr[0].isBlank())
                .map(arr -> List.of(arr[0].trim(), arr[1].trim()))
                .collect(Collectors.toList());
            if (filter.isEmpty()) {
                filter = null;
            }
        }
        return repo.findAll(
            body.getSort(), 
            body.getOrder(), 
            body.getSearchField(), 
            body.getKeyword(), 
            body.getPage(), 
            body.getSize(),
            filter
        );
    }
}
