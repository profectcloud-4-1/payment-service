package profect.group1.goormdotcom.user.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import profect.group1.goormdotcom.user.application.UserService;
import profect.group1.goormdotcom.user.presentation.dto.request.RegisterRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.response.MeResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.request.ListRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.response.ListResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.response.RegisterResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.request.ApproveRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.EditRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.LoginRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.response.LoginResponseDto;
import profect.group1.goormdotcom.user.application.dto.CreateUserDto;
import profect.group1.goormdotcom.user.domain.User;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController implements UserApiDocs {
    
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponseDto> register(@RequestBody RegisterRequestDto body) {
        CreateUserDto userDto = CreateUserDto.builder()
            .name(body.getName())
            .email(body.getEmail())
            .password(body.getPassword())
            .role(body.getRole())
            .brandId(body.getBrandId())
            .build();

        try {
            User user = service.register(userDto);
            return ApiResponse.onSuccess(RegisterResponseDto.of(user));
        } catch (Exception e) {
            String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
            String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
            switch (e.getMessage()) {
                case "Invalid password":
                    code = ErrorStatus.AUTH_INVALID_PASSWORD.getCode();
                    message = ErrorStatus.AUTH_INVALID_PASSWORD.getMessage();
                    break;
                case "Email already exists":
                    code = ErrorStatus.AUTH_EMAIL_ALREADY_EXISTS.getCode();
                    message = ErrorStatus.AUTH_EMAIL_ALREADY_EXISTS.getMessage();
                    break;
            }
            return ApiResponse.onFailure(String.valueOf(code), e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto body) {
        try {
            String token = service.login(body.getEmail(), body.getPassword());
            return ApiResponse.onSuccess(LoginResponseDto.of(token));
        } catch (Exception e) {
            String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
            String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();

            switch (e.getMessage()) {
                case "Invalid credentials":
                    code = ErrorStatus.AUTH_NOT_EXISTS.getCode();
                    message = ErrorStatus.AUTH_NOT_EXISTS.getMessage();
                    break;
            }
            return ApiResponse.onFailure(String.valueOf(code), message, null);
        }
    }

    @PostMapping("/leave")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Object> leave(HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("jwtClaims");
        // soft remove
        service.leave(claims.getSubject());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasRole('MASTER')")
    public ApiResponse<Object> approve(HttpServletRequest request, @PathVariable String userId, @RequestBody ApproveRequestDto body) {
        Claims claims = (Claims) request.getAttribute("jwtClaims");
        service.approve(userId, claims.getSubject(), body.isApproval());
        return ApiResponse.onSuccess(null);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Object> edit(HttpServletRequest request, @RequestBody EditRequestDto body) {
        Claims claims = (Claims) request.getAttribute("jwtClaims");
        try {
        service.edit(claims.getSubject(), body.getName(), body.getEmail());
            return ApiResponse.onSuccess(null);
        } catch (Exception e) {
            String code = ErrorStatus._INTERNAL_SERVER_ERROR.getCode();
            String message = ErrorStatus._INTERNAL_SERVER_ERROR.getMessage();
            switch (e.getMessage()) {
                case "Email already exists":
                    code = ErrorStatus.AUTH_EMAIL_ALREADY_EXISTS.getCode();
                    message = ErrorStatus.AUTH_EMAIL_ALREADY_EXISTS.getMessage();
                    break;
            }
            return ApiResponse.onFailure(String.valueOf(code), message, null);
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('MASTER')")
    public ApiResponse<Object> delete(HttpServletRequest request, @PathVariable String userId) {
        Claims claims = (Claims) request.getAttribute("jwtClaims");
        service.delete(userId, claims.getSubject());
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/me")
    public ApiResponse<MeResponseDto> me(HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("jwtClaims");
        User user = service.findById(claims.getSubject()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ApiResponse.onSuccess(MeResponseDto.of(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('MASTER')")
    public ApiResponse<ListResponseDto> users(ListRequestDto body) {
        List<User> users = service.findAllBy(body);
        return ApiResponse.onSuccess(ListResponseDto.of(users));
    }

    // NOTE: API 인증 예시 코드. 참고용으로 남겨둠.
    // @GetMapping("/auth-guard-test")
    // public ApiResponse<Object> authTest(HttpServletRequest request) {
    //     Claims claims = (Claims) request.getAttribute("jwtClaims");
    //     if (claims == null) {
    //         return ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), ErrorStatus._UNAUTHORIZED.getMessage(), null);
    //     }
    //     return ApiResponse.onSuccess(claims);
    // }

    // @GetMapping("/role-guard-test")
    // @PreAuthorize("hasRole('MASTER')")
    // public ApiResponse<Object> roleGuardTest(HttpServletRequest request) {
    //     Claims claims = (Claims) request.getAttribute("jwtClaims");
    //     return ApiResponse.onSuccess(claims);
    // }
}
