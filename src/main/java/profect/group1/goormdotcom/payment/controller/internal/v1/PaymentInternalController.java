package profect.group1.goormdotcom.payment.controller.internal.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.common.auth.LoginUser;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/payments")
public class PaymentInternalController implements PaymentInternalApiDocs {
    private final PaymentService paymentService;

    @Override
    @PostMapping("/toss/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiResponse<PaymentCancelResponseDto> tossPaymentCancel(@ModelAttribute @Valid PaymentCancelRequestDto paymentCancelRequestDto, @LoginUser UUID userId) {
        Payment payment = paymentService.tossPaymentCancel(userId, paymentCancelRequestDto);
        return ApiResponse.onSuccess(new PaymentCancelResponseDto(payment.getPaymentKey(), "PAY0003", List.of()));
    }
}
