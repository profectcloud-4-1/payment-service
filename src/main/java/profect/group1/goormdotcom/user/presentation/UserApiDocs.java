package profect.group1.goormdotcom.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.RequestBody;

import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.user.presentation.dto.request.ApproveRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.EditRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.ListRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.LoginRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.request.RegisterRequestDto;
import profect.group1.goormdotcom.user.presentation.dto.response.ListResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.response.LoginResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.response.MeResponseDto;
import profect.group1.goormdotcom.user.presentation.dto.response.RegisterResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Tag(name = "User", description = "사용자 API")
public interface UserApiDocs {

	@Operation(summary = "회원가입")
	ApiResponse<RegisterResponseDto> register(@RequestBody RegisterRequestDto body);

	@Operation(summary = "로그인")
	ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto body);

	@Operation(summary = "회원 탈퇴", security = { @SecurityRequirement(name = "bearerAuth") })
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = "{\"code\":\"COMMON200\",\"message\":\"성공입니다.\"}"))
	)
	ApiResponse<Object> leave(HttpServletRequest request);

	@Operation(summary = "사용자(SELLER) 승인/거절", description = "MASTER only", security = { @SecurityRequirement(name = "bearerAuth") })
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = "{\"code\":\"COMMON200\",\"message\":\"성공입니다.\"}"))
	)
	ApiResponse<Object> approve(
		HttpServletRequest request,
		@Parameter(description = "대상 사용자 ID") String userId,
		@RequestBody ApproveRequestDto body
	);

	@Operation(summary = "내 정보 수정", security = { @SecurityRequirement(name = "bearerAuth") })
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = "{\"code\":\"COMMON200\",\"message\":\"성공입니다.\"}"))
	)
	ApiResponse<Object> edit(HttpServletRequest request, @RequestBody EditRequestDto body);

	@Operation(summary = "사용자 삭제", description = "MASTER only", security = { @SecurityRequirement(name = "bearerAuth") })
	@io.swagger.v3.oas.annotations.responses.ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "success", value = "{\"code\":\"COMMON200\",\"message\":\"성공입니다.\"}"))
	)
	ApiResponse<Object> delete(HttpServletRequest request, @Parameter(description = "대상 사용자 ID") String userId);

    @Operation(summary = "내 정보 조회", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<MeResponseDto> me(HttpServletRequest request);

	@Operation(summary = "사용자 목록 조회", description = "검색/필터/정렬 가능", security = { @SecurityRequirement(name = "bearerAuth") })
	ApiResponse<ListResponseDto> users(@ParameterObject ListRequestDto body);
}
