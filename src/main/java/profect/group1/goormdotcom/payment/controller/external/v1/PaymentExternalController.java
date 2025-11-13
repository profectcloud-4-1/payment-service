package profect.group1.goormdotcom.payment.controller.external.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.common.auth.LoginUser;
import profect.group1.goormdotcom.common.dto.UserContext;
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
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<PaymentSuccessResponseDto> tossPaymentSuccess(@ModelAttribute @Validated PaymentSuccessRequestDto paymentSuccessRequestDto ,
                                                                     @LoginUser UUID userId) {
        return ApiResponse.onSuccess(paymentService.tossPaymentSuccess(paymentSuccessRequestDto, userId));
    }

    @Override
    @GetMapping("/toss/fail")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<Void> tossPaymentFail(@ModelAttribute @Valid PaymentFailRequestDto paymentFailRequestDto, @LoginUser UUID userId) {
        paymentService.tossPaymentFail(userId, paymentFailRequestDto);
        return ApiResponse.onSuccess(null);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<PaymentSearchResponseDto> searchPayment (@ModelAttribute PaymentSearchRequestDto paymentSearchRequestDto,
                                                                Pageable pageable, @LoginUser UUID userId) {
        return ApiResponse.onSuccess(paymentService.search(userId, paymentSearchRequestDto, pageable));
    }

}
