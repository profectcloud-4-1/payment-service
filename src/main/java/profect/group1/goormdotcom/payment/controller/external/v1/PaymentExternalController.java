package profect.group1.goormdotcom.payment.controller.external.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.*;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSearchResponseDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.service.PaymentService;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentExternalController implements PaymentApiDocs {
    private final PaymentService paymentService;

    @Override
    @GetMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponseDto> tossPaymentSuccess(@ModelAttribute @Validated PaymentSuccessRequestDto paymentSuccessRequestDto ,
                                                                     @RequestParam UUID userId) {
        return ApiResponse.onSuccess(paymentService.tossPaymentSuccess(paymentSuccessRequestDto, userId));
    }

    @Override
    @GetMapping("/toss/fail")
    public ApiResponse<Void> tossPaymentFail(@ModelAttribute @Valid PaymentFailRequestDto paymentFailRequestDto) {
        paymentService.tossPaymentFail(paymentFailRequestDto);
        return ApiResponse.onSuccess(null);
    }

    @Override
    @GetMapping
    //TODO: @AuthenticationPrincipal User user 추가
    public ApiResponse<PaymentSearchResponseDto> searchPayment (@ModelAttribute PaymentSearchRequestDto paymentSearchRequestDto,
                                                                Pageable pageable) {
        //임시
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        return ApiResponse.onSuccess(paymentService.search(userId, paymentSearchRequestDto, pageable));
    }

}
