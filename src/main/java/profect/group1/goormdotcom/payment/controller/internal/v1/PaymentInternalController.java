package profect.group1.goormdotcom.payment.controller.internal.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.payment.controller.external.v1.PaymentController;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.service.PaymentService;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
