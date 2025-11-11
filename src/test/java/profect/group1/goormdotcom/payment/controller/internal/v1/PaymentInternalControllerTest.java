package profect.group1.goormdotcom.payment.controller.internal.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import profect.group1.goormdotcom.apiPayload.exceptions.ExceptionAdvice;
import profect.group1.goormdotcom.payment.controller.external.v1.PaymentExternalController;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.domain.Payment;
import profect.group1.goormdotcom.payment.service.PaymentService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentInternalController 테스트")
public class PaymentInternalControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private PaymentInternalController paymentInternalController;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentInternalController)
                .setControllerAdvice(new ExceptionAdvice())
                .build();
    }

    @Test
    @DisplayName("성공 - 결제 취소 요청")
    void tossPaymentCancel_Success() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        String paymentKey = UUID.randomUUID().toString();
        String cancelReason = "고객 요청";

        Payment mockPayment = Payment.create(
                UUID.randomUUID(),
                orderId,
                "Test Order",
                paymentKey,
                10000L
        );
        mockPayment.setStatus("PAY0003"); // 취소 요청 중 상태

        when(paymentService.tossPaymentCancel(any(PaymentCancelRequestDto.class)))
                .thenReturn(mockPayment);

        // when & then
        mockMvc.perform(post("/internal/v1/payments/toss/cancel")
                        .param("orderId", orderId.toString())
                        .param("cancelReason", cancelReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.paymentKey").value(paymentKey))
                .andExpect(jsonPath("$.result.status").value("PAY0003"))
                .andDo(print());
    }
}
