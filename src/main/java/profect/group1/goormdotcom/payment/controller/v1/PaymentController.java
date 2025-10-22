package profect.group1.goormdotcom.payment.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentResponseDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentCreateRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.controller.mapper.PaymentDtoMapper;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.service.PaymentService;
import profect.group1.goormdotcom.user.domain.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentApiDocs {
    private final PaymentService paymentService;

    //TODO: auth 넣기

    @Override
    @PostMapping
    public ApiResponse<PaymentResponseDto> requestPayment(@AuthenticationPrincipal User user, @RequestBody @Valid PaymentCreateRequestDto paymentRequestDto) {

        Payment payment = paymentService.requestPayment(paymentRequestDto, user);
        return ApiResponse.onSuccess(PaymentDtoMapper.toPaymentDto(payment));
    }

    @Override
    @GetMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponseDto> tossPaymentSuccess(@ModelAttribute @Valid PaymentSuccessRequestDto paymentSuccessRequestDto) {
        return ApiResponse.onSuccess(paymentService.tossPaymentSuccess(paymentSuccessRequestDto));
    }

    @Override
    @GetMapping("/toss/fail")
    public ApiResponse<Void> tossPaymentFail(@ModelAttribute @Valid PaymentFailRequestDto paymentFailRequestDto) {
        paymentService.tossPaymentFail(paymentFailRequestDto);
        return ApiResponse.onSuccess(null);
    }

    @Override
    @PostMapping("/toss/cancel")
    public ApiResponse<PaymentCancelResponseDto> tossPaymentCancel(@ModelAttribute @Valid PaymentCancelRequestDto paymentCancelRequestDto,
                                                                   @RequestParam String paymentKey) {
        return ApiResponse.onSuccess(paymentService.tossPaymentCancel(paymentCancelRequestDto, paymentKey));
    }
}
