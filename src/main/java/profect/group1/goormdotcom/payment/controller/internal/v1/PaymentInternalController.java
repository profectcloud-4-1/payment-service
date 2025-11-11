package profect.group1.goormdotcom.payment.controller.internal.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.service.PaymentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/payments")
public class PaymentInternalController implements PaymentInternalApiDocs {
    private final PaymentService paymentService;

    @Override
    @PostMapping("/toss/cancel")
    public ApiResponse<PaymentCancelResponseDto> tossPaymentCancel(@ModelAttribute @Valid PaymentCancelRequestDto paymentCancelRequestDto) {
        Payment payment = paymentService.tossPaymentCancel(paymentCancelRequestDto);
        return ApiResponse.onSuccess(new PaymentCancelResponseDto(payment.getPaymentKey(), "PAY0003", List.of()));
    }
}
