package profect.group1.goormdotcom.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import profect.group1.goormdotcom.apiPayload.code.status.ErrorStatus;
import profect.group1.goormdotcom.apiPayload.exceptions.handler.PaymentHandler;
import profect.group1.goormdotcom.payment.config.TossPaymentConfig;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCancelRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentCanceledEvent;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentFailRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentSearchRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.request.PaymentSuccessRequestDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentCancelResponseDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSearchResponseDto;
import profect.group1.goormdotcom.payment.controller.external.v1.dto.response.PaymentSuccessResponseDto;
import profect.group1.goormdotcom.payment.infrastructure.client.OrderClient;
import profect.group1.goormdotcom.payment.infrastructure.client.dto.PaymentSuccessResultDto;
import profect.group1.goormdotcom.payment.repository.PaymentHistoryRepository;
import profect.group1.goormdotcom.payment.repository.PaymentRepository;
import profect.group1.goormdotcom.payment.repository.entity.PaymentEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 테스트")
public class PaymentServiceTest {

    @Spy
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private TossPaymentConfig tossPaymentConfig;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private WebClient tossWebClient;
    @Mock
    private OrderClient orderClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    private PaymentEntity createPaymentEntity(UUID id, UUID orderId, String paymentKey, Long amount, String status) {
        return new PaymentEntity(id, UUID.randomUUID(), orderId, "Test Order", status, amount, 0L, paymentKey, LocalDateTime.now(), null);
    }

    @BeforeEach
    void setUp() {
        lenient().when(tossWebClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodyUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        lenient().when(tossWebClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        lenient().when(tossPaymentConfig.getSecretKey()).thenReturn("test_secret_key");
    }

    @Nested
    @DisplayName("tossPaymentSuccess 메서드 테스트")
    class TossPaymentSuccessTest {

        @Test
        @DisplayName("성공 - 결제 승인 및 관련 정보 저장")
        void tossPaymentSuccess_Success() throws JsonProcessingException {
            UUID userId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            String paymentKey = UUID.randomUUID().toString();
            Long amount = 15000L;

            PaymentSuccessRequestDto requestDto = new PaymentSuccessRequestDto();
            requestDto.setOrderId(orderId);
            requestDto.setPaymentKey(paymentKey);
            requestDto.setAmount(amount);
            requestDto.setOrderName("Test Order");

            PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), orderId, paymentKey, amount, "PAY0001");
            PaymentSuccessResponseDto tossResponse = new PaymentSuccessResponseDto("test_mid", paymentKey, orderId.toString(), "Test Order", "DONE", OffsetDateTime.now(), OffsetDateTime.now(), "KRW", "15000", "15000", "15000", "CARD");

            when(paymentRepository.findByOrderIdAndStatus(orderId, "PAY0000")).thenReturn(Optional.empty());
            when(paymentRepository.existsByPaymentKey(paymentKey)).thenReturn(false);
            when(responseSpec.bodyToMono(PaymentSuccessResponseDto.class)).thenReturn(Mono.just(tossResponse));
            when(paymentRepository.saveAndFlush(any(PaymentEntity.class))).thenReturn(paymentEntity);
            doNothing().when(orderClient).notifyPaymentSuccessResult(any(UUID.class), any(PaymentSuccessResultDto.class));

            PaymentSuccessResponseDto result = paymentService.tossPaymentSuccess(requestDto, userId);

            assertThat(result)
                    .isNotNull()
                    .extracting(PaymentSuccessResponseDto::status)
                    .isEqualTo("DONE");
            verify(paymentRepository, times(1)).saveAndFlush(any(PaymentEntity.class));
            verify(paymentHistoryRepository, times(1)).save(any());
            verify(orderClient, times(1)).notifyPaymentSuccessResult(any(UUID.class), any(PaymentSuccessResultDto.class));
        }

        @Test
        @DisplayName("실패 - 중복 결제 요청")
        void tossPaymentSuccess_DuplicateRequest_ThrowsException() {
            PaymentSuccessRequestDto requestDto = new PaymentSuccessRequestDto();
            requestDto.setOrderId(UUID.randomUUID());
            PaymentEntity existingPayment = createPaymentEntity(UUID.randomUUID(), requestDto.getOrderId(), "key", 1000L, "PAY0000");
            when(paymentRepository.findByOrderIdAndStatus(any(), anyString())).thenReturn(Optional.of(existingPayment));

            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentSuccess(requestDto, UUID.randomUUID()));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._DUPLICATE_PAYMENT_REQUEST);
        }

        @Test
        @DisplayName("실패(멱등성) - 이미 처리된 paymentKey")
        void tossPaymentSuccess_Idempotency_ReturnsNull() {
            // given
            PaymentSuccessRequestDto requestDto = new PaymentSuccessRequestDto();
            requestDto.setOrderId(UUID.randomUUID());
            requestDto.setPaymentKey("existing_key");

            when(paymentRepository.existsByPaymentKey("existing_key")).thenReturn(true);

            // when
            PaymentSuccessResponseDto result = paymentService.tossPaymentSuccess(requestDto, UUID.randomUUID());

            // then
            assertThat(result).isNull();
            verify(paymentRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("실패 - 결제 금액이 1000원 미만")
        void tossPaymentSuccess_AmountLessThan1000_ThrowsException() {
            // given
            PaymentSuccessRequestDto requestDto = new PaymentSuccessRequestDto();
            requestDto.setOrderId(UUID.randomUUID());
            requestDto.setPaymentKey("some_key");
            requestDto.setAmount(999L);

            // when & then
            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentSuccess(requestDto, UUID.randomUUID()));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("tossPaymentFail 메서드 테스트")
    class TossPaymentFailTest {

        @Test
        @DisplayName("성공 - 결제 실패 처리")
        void tossPaymentFail_Success() {
            UUID orderId = UUID.randomUUID();
            PaymentFailRequestDto requestDto = new PaymentFailRequestDto();
            requestDto.setOrderId(orderId);

            PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), orderId, "some_key", 10000L, "PAY0000");
            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentEntity));

            paymentService.tossPaymentFail(requestDto);

            verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
            verify(paymentHistoryRepository, times(1)).save(any());
            verify(orderClient, times(1)).notifyPaymentFailResult(any(), any());
            assertThat(paymentEntity.getStatus()).isEqualTo("PAY0002");
        }

        @Test
        @DisplayName("실패 - 결제 정보를 찾을 수 없음")
        void tossPaymentFail_PaymentNotFound_ThrowsException() {
            // given
            PaymentFailRequestDto requestDto = new PaymentFailRequestDto();
            requestDto.setOrderId(UUID.randomUUID());

            when(paymentRepository.findByOrderId(any(UUID.class))).thenReturn(Optional.empty());

            // when & then
            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentFail(requestDto));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._PAYMENT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("tossPaymentCancel 메서드 테스트")
    class TossPaymentCancelTest {

        @Test
        @DisplayName("성공 - 결제 취소 요청")
        void tossPaymentCancel_Success() {
            UUID orderId = UUID.randomUUID();
            PaymentCancelRequestDto requestDto = PaymentCancelRequestDto.builder().orderId(orderId).build();
            PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), orderId, "some_key", 10000L, "PAY0001");
            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentEntity));

            paymentService.tossPaymentCancel(requestDto);

            verify(paymentRepository, times(1)).save(paymentEntity);
            verify(paymentHistoryRepository, times(1)).save(any());
            verify(eventPublisher, times(1)).publishEvent(any(PaymentCanceledEvent.class));
            assertThat(paymentEntity.getStatus()).isEqualTo("PAY0003");
        }

        @Test
        @DisplayName("실패 - 이미 취소된 결제")
        void tossPaymentCancel_AlreadyCanceled_ThrowsException() {
            UUID orderId = UUID.randomUUID();
            PaymentCancelRequestDto requestDto = PaymentCancelRequestDto.builder().orderId(orderId).build();
            PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), orderId, "some_key", 10000L, "PAY0004");
            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentEntity));

            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentCancel(requestDto));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._ALREADY_CANCELED_REQUEST);
        }

        @Test
        @DisplayName("실패 - 결제 정보를 찾을 수 없음")
        void tossPaymentCancel_PaymentNotFound_ThrowsException() {
            // given
            PaymentCancelRequestDto requestDto = PaymentCancelRequestDto.builder().orderId(UUID.randomUUID()).build();
            when(paymentRepository.findByOrderId(any(UUID.class))).thenReturn(Optional.empty());

            // when & then
            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentCancel(requestDto));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._PAYMENT_NOT_FOUND);
        }

        @Test
        @DisplayName("실패 - 취소할 수 없는 결제 상태")
        void tossPaymentCancel_InvalidStatus_ThrowsException() {
            // given
            UUID orderId = UUID.randomUUID();
            PaymentCancelRequestDto requestDto = PaymentCancelRequestDto.builder().orderId(orderId).build();
            PaymentEntity paymentEntity = createPaymentEntity(UUID.randomUUID(), orderId, "some_key", 10000L, "PAY0000"); // Not PAY0001
            when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(paymentEntity));

            // when & then
            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.tossPaymentCancel(requestDto));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._INVALID_PAYMENT_STATUS);
        }
    }

    @Nested
    @DisplayName("executeCancel 메서드 테스트")
    class ExecuteCancelTest {

        @Test
        @DisplayName("성공 - 결제 취소 실행")
        void executeCancel_Success() {
            PaymentCanceledEvent event = new PaymentCanceledEvent(UUID.randomUUID(), "paymentKey", PaymentCancelRequestDto.builder().build());
            PaymentCancelResponseDto.CancelEntry cancelEntry = new PaymentCancelResponseDto.CancelEntry(10000L, "reason", 0L, OffsetDateTime.now(), "tx_key", "DONE");
            PaymentCancelResponseDto tossResponse = new PaymentCancelResponseDto("paymentKey", "CANCELED", Collections.singletonList(cancelEntry));

            when(responseSpec.bodyToMono(PaymentCancelResponseDto.class)).thenReturn(Mono.just(tossResponse));
            doNothing().when(paymentService).updatePaymentCancelStatus(any(), any());

            paymentService.executeCancel(event);

            verify(paymentService, times(1)).updatePaymentCancelStatus(eq(event.paymentId()), any());
        }

        @Test
        @DisplayName("실패 - Toss API 호출 실패 시 예외 처리")
        void executeCancel_ApiFails() {
            PaymentCanceledEvent event = new PaymentCanceledEvent(UUID.randomUUID(), "paymentKey", PaymentCancelRequestDto.builder().build());
            when(responseSpec.bodyToMono(PaymentCancelResponseDto.class)).thenReturn(Mono.error(new RuntimeException("API Error")));

            paymentService.executeCancel(event);

            verify(paymentService, never()).updatePaymentCancelStatus(any(), any());
        }

        @Test
        @DisplayName("실패 - Toss 응답이 비어있음")
        void executeCancel_TossResponseEmpty_ThrowsException() {
            // given
            PaymentCanceledEvent event = new PaymentCanceledEvent(UUID.randomUUID(), "paymentKey", PaymentCancelRequestDto.builder().build());
            when(responseSpec.bodyToMono(PaymentCancelResponseDto.class)).thenReturn(Mono.just(new PaymentCancelResponseDto("paymentKey", "CANCELED", Collections.emptyList())));

            // when
            paymentService.executeCancel(event);

            // then
            verify(paymentService, never()).updatePaymentCancelStatus(any(), any());
        }

        @Test
        @DisplayName("실패 - Toss 응답의 취소 상태가 DONE이 아님")
        void executeCancel_TossCancelStatusNotDone_ThrowsException() {
            // given
            PaymentCanceledEvent event = new PaymentCanceledEvent(UUID.randomUUID(), "paymentKey", PaymentCancelRequestDto.builder().build());
            PaymentCancelResponseDto.CancelEntry cancelEntry = new PaymentCancelResponseDto.CancelEntry(10000L, "reason", 0L, OffsetDateTime.now(), "tx_key", "FAILED");
            PaymentCancelResponseDto tossResponse = new PaymentCancelResponseDto("paymentKey", "CANCELED", Collections.singletonList(cancelEntry));
            when(responseSpec.bodyToMono(PaymentCancelResponseDto.class)).thenReturn(Mono.just(tossResponse));

            // when
            paymentService.executeCancel(event);

            // then
            verify(paymentService, never()).updatePaymentCancelStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("updatePaymentCancelStatus 메서드 테스트")
    class UpdatePaymentCancelStatusTest {
        @Test
        @DisplayName("성공 - 결제 취소 상태 업데이트")
        void updatePaymentCancelStatus_Success() {
            // given
            UUID paymentId = UUID.randomUUID();
            PaymentEntity paymentEntity = createPaymentEntity(paymentId, UUID.randomUUID(), "key", 10000L, "PAY0003");
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(paymentEntity));

            // when
            paymentService.updatePaymentCancelStatus(paymentId, LocalDateTime.now());

            // then
            verify(paymentRepository, times(1)).saveAndFlush(paymentEntity);
            verify(paymentHistoryRepository, times(1)).save(any());
            assertThat(paymentEntity.getStatus()).isEqualTo("PAY0004");
        }

        @Test
        @DisplayName("실패 - 결제 정보를 찾을 수 없음")
        void updatePaymentCancelStatus_PaymentNotFound_ThrowsException() {
            // given
            UUID paymentId = UUID.randomUUID();
            when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

            // when & then
            PaymentHandler exception = assertThrows(PaymentHandler.class, () -> paymentService.updatePaymentCancelStatus(paymentId, LocalDateTime.now()));
            assertThat(exception.getCode()).isEqualTo(ErrorStatus._PAYMENT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("search 메서드 테스트")
    class SearchTest {
        @Test
        @DisplayName("성공 - 상태 필터로 검색")
        void search_WithStatusFilter_ReturnsFiltered() {
            // given
            UUID userId = UUID.randomUUID();
            PaymentSearchRequestDto requestDto = PaymentSearchRequestDto.of("PAY0001", null, null, null, null);

            PaymentEntity successPayment = createPaymentEntity(UUID.randomUUID(), UUID.randomUUID(), "key1", 1000L, "PAY0001");
            PaymentEntity failPayment = createPaymentEntity(UUID.randomUUID(), UUID.randomUUID(), "key2", 2000L, "PAY0002");
            Slice<PaymentEntity> slice = new SliceImpl<>(List.of(successPayment, failPayment));
            when(paymentRepository.findAllByUserId(any(), any())).thenReturn(slice);

            // when
            PaymentSearchResponseDto result = paymentService.search(userId, requestDto, PageRequest.of(0, 10));

            // then
            assertThat(result.items())
                    .singleElement()
                    .extracting(PaymentSearchResponseDto.Item::status)
                    .isEqualTo("PAY0001");
        }

        @Test
        @DisplayName("성공 - 날짜 필터로 검색")
        void search_WithDateFilter_ReturnsFiltered() {
            // given
            UUID userId = UUID.randomUUID();
            PaymentSearchRequestDto requestDto = PaymentSearchRequestDto.of(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), null, null);

            PaymentEntity recentPayment = createPaymentEntity(UUID.randomUUID(), UUID.randomUUID(), "key1", 1000L, "PAY0001");
            recentPayment.setApprovedAt(LocalDateTime.now());
            PaymentEntity oldPayment = createPaymentEntity(UUID.randomUUID(), UUID.randomUUID(), "key2", 2000L, "PAY0001");
            oldPayment.setApprovedAt(LocalDateTime.now().minusDays(2));
            Slice<PaymentEntity> slice = new SliceImpl<>(List.of(recentPayment, oldPayment));
            when(paymentRepository.findAllByUserId(any(), any())).thenReturn(slice);

            // when
            PaymentSearchResponseDto result = paymentService.search(userId, requestDto, PageRequest.of(0, 10));

            // then
            assertThat(result.items())
                    .singleElement()
                    .extracting(PaymentSearchResponseDto.Item::paymentId)
                    .isEqualTo(recentPayment.getId());
        }
    }
}