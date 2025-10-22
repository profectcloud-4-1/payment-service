package profect.group1.goormdotcom.payment.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentCreateRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.user.domain.User;

@Tag(name = "결제 관리", description = "결제 관련 API")
public interface PaymentApiDocs {
    @Operation(
            summary = "결제 요청 API",
            description = "사용자가 주문에 대해 결제를 요청하면 새로운 결제가 생성되고 상태는 PENDING으로 설정됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공입니다"),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 (예: 1000원 미만 결제)",
                    content = @Content(schema = @Schema(implementation = profect.group1.goormdotcom.apiPayload.ApiResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = profect.group1.goormdotcom.apiPayload.ApiResponse.class))
            )


    })
    profect.group1.goormdotcom.apiPayload.ApiResponse<PaymentResponseDto> requestPayment(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PaymentCreateRequestDto paymentRequestDto
    );

    @Operation(
            summary = "결제 성공 리디렉션 API",
            description = "결제 성공 시 리디렉션되는 API입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공입니다"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = profect.group1.goormdotcom.apiPayload.ApiResponse.class))
            )
    })
    profect.group1.goormdotcom.apiPayload.ApiResponse<PaymentSuccessResponseDto> tossPaymentSuccess(
            @ModelAttribute @Valid PaymentSuccessRequestDto paymentSuccessRequestDto
    );

    @Operation(
            summary = "결제 실패 리디렉션 API",
            description = "결제 실패 시 리디렉션되는 API입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공입니다"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = profect.group1.goormdotcom.apiPayload.ApiResponse.class))
            )
    })
    profect.group1.goormdotcom.apiPayload.ApiResponse<Void> tossPaymentFail(
            @ModelAttribute @Valid PaymentFailRequestDto paymentFailRequestDto
    );

    @Operation(
            summary = "결제 취소 API",
            description = "승인된 결제를 취소합니다. 토스에서 발급된 paymentKey(쿼리 파라미터)와 취소 사유/금액 등은 ModelAttribute DTO로 전달됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공입니다"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = profect.group1.goormdotcom.apiPayload.ApiResponse.class))
            )
    })
    profect.group1.goormdotcom.apiPayload.ApiResponse<PaymentCancelResponseDto> tossPaymentCancel(
            @ModelAttribute @Valid profect.group1.goormdotcom.payment.controller.dto.request.PaymentCancelRequestDto paymentCancelRequestDto,
            @Parameter(description = "토스 결제 키(PG에서 발급된 paymentKey)", required = true)
            @RequestParam String paymentKey
    );
}
