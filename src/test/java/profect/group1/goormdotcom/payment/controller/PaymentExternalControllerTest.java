package profect.group1.goormdotcom.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.apiPayload.exceptions.ExceptionAdvice;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.PaymentHandler;
import profect.group1.goormdotcom.common.auth.LoginUserArgumentResolver;
import profect.group1.goormdotcom.payment.controller.external.v1.PaymentExternalController;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentSearchRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSearchResponseDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.domain.enums.Status;
import profect.group1.goormdotcom.payment.service.PaymentService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentExternalController 테스트")
public class PaymentExternalControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private PaymentExternalController paymentExternalController;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentExternalController)
                .setControllerAdvice(new ExceptionAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private PaymentSuccessResponseDto createPaymentSuccessResponseDto(String status) {
        return new PaymentSuccessResponseDto(
                "test_mid",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Test Order",
                status,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                "KRW",
                "15000",
                "15000",
                "15000",
                "CARD"
        );
    }

    @Nested
    @DisplayName("GET /api/v1/payments/toss/success")
    class TossPaymentSuccessTest {

        private final String BASE_URL = "/api/v1/payments/toss/success";

        @Test
        @DisplayName("성공 - 결제 성공 처리 및 응답 반환")
        void tossPaymentSuccess_Success() throws Exception {
            // given
            LoginUserArgumentResolver loginUserArgumentResolver = mock(LoginUserArgumentResolver.class);
            when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

            mockMvc = MockMvcBuilders.standaloneSetup(paymentExternalController)
                    .setControllerAdvice(new ExceptionAdvice())
                    .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), loginUserArgumentResolver)
                    .build();

            UUID orderId = UUID.randomUUID();
            String paymentKey = UUID.randomUUID().toString();
            Long amount = 15000L;

            PaymentSuccessResponseDto serviceResponse = createPaymentSuccessResponseDto("DONE");

            when(paymentService.tossPaymentSuccess(any(PaymentSuccessRequestDto.class), any(UUID.class)))
                    .thenReturn(serviceResponse);

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .param("orderId", orderId.toString())
                            .param("orderName", "Test Order")
                            .param("paymentKey", paymentKey)
                            .param("amount", amount.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패 - 필수 파라미터 누락 시 BadRequest")
        void tossPaymentSuccess_MissingParameter_BadRequest() throws Exception {
            // given
            UUID orderId = UUID.randomUUID();

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .param("orderId", orderId.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - PaymentService에서 예외 발생 시 처리")
        void tossPaymentSuccess_ServiceThrowsException_HandlesError() throws Exception {
            // given
            LoginUserArgumentResolver loginUserArgumentResolver = mock(LoginUserArgumentResolver.class);
            when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

            mockMvc = MockMvcBuilders.standaloneSetup(paymentExternalController)
                    .setControllerAdvice(new ExceptionAdvice())
                    .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), loginUserArgumentResolver)
                    .build();

            UUID orderId = UUID.randomUUID();
            String paymentKey = UUID.randomUUID().toString();
            Long amount = 15000L;

            when(paymentService.tossPaymentSuccess(any(PaymentSuccessRequestDto.class), any(UUID.class)))
                    .thenThrow(new PaymentHandler(ErrorStatus._DUPLICATE_PAYMENT_REQUEST));

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .param("orderId", orderId.toString())
                            .param("orderName", "Test Order")
                            .param("paymentKey", paymentKey)
                            .param("amount", amount.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/payments/toss/fail")
    class TossPaymentFailTest {

        private final String BASE_URL = "/api/v1/payments/toss/fail";

        @Test
        @DisplayName("성공 - 결제 실패 처리 및 응답 반환")
        void tossPaymentFail_Success() throws Exception {
            // given
            LoginUserArgumentResolver loginUserArgumentResolver = mock(LoginUserArgumentResolver.class);
            when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

            mockMvc = MockMvcBuilders.standaloneSetup(paymentExternalController)
                    .setControllerAdvice(new ExceptionAdvice())
                    .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), loginUserArgumentResolver)
                    .build();

            UUID orderId = UUID.randomUUID();
            String errorCode = "PAY_FAILED";
            String errorMessage = "결제 실패";

            doNothing().when(paymentService).tossPaymentFail(any(UUID.class), any(PaymentFailRequestDto.class));

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .param("orderId", orderId.toString())
                            .param("code", errorCode)
                            .param("message", errorMessage)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/payments")
    class SearchPaymentTest {

        private final String BASE_URL = "/api/v1/payments";

        @Test
        @DisplayName("성공 - 결제 내역 검색 및 응답 반환")
        void searchPayment_Success() throws Exception {
            // given
            LoginUserArgumentResolver loginUserArgumentResolver = mock(LoginUserArgumentResolver.class);
            when(loginUserArgumentResolver.supportsParameter(any())).thenReturn(true);
            when(loginUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

            mockMvc = MockMvcBuilders.standaloneSetup(paymentExternalController)
                    .setControllerAdvice(new ExceptionAdvice())
                    .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(), loginUserArgumentResolver)
                    .build();

            PaymentSearchResponseDto.Item item = new PaymentSearchResponseDto.Item(
                    UUID.randomUUID(),
                    "Test Order",
                    10000L,
                    Status.SUCCESS.name(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null
            );
            PaymentSearchResponseDto.Pagination pagination = new PaymentSearchResponseDto.Pagination(0, 10, false, true);
            PaymentSearchResponseDto serviceResponse = new PaymentSearchResponseDto(Collections.singletonList(item), pagination);

            when(paymentService.search(any(UUID.class), any(PaymentSearchRequestDto.class), any(Pageable.class)))
                    .thenReturn(serviceResponse);

            // when & then
            mockMvc.perform(get(BASE_URL)
                            .param("status", "SUCCESS")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }
}
