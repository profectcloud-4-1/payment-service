package profect.group1.goormdotcom.payment.controller.internal.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentCancelResponseDto;

@Tag(name = "결제 내부", description = "결제 관련 내부 API")
public interface PaymentInternalApiDocs {
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
            @ModelAttribute @Valid profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto paymentCancelRequestDto
    );
}
