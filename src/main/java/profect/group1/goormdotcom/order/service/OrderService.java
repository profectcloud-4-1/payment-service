package profect.group1.goormdotcom.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; //?
import profect.group1.goormdotcom.order.client.DeliveryClient; //?
import profect.group1.goormdotcom.order.client.PaymentClient;
import profect.group1.goormdotcom.order.client.StockClient;
import profect.group1.goormdotcom.order.controller.dto.OrderRequestDto;
import profect.group1.goormdotcom.order.controller.dto.OrderResponseDto;
import profect.group1.goormdotcom.order.repository.CommonCodeRepository;
import profect.group1.goormdotcom.order.repository.OrderProductRepository; //?
import profect.group1.goormdotcom.order.repository.OrderRepository;
import profect.group1.goormdotcom.order.repository.OrderStatusRepository;
import profect.group1.goormdotcom.order.repository.entity.CommonCodeEntity;
import profect.group1.goormdotcom.order.repository.entity.OrderEntity;
import profect.group1.goormdotcom.order.repository.entity.OrderProductEntity;
import profect.group1.goormdotcom.order.repository.entity.OrderStatusEntity;

@Slf4j
@Service
// @Transactional
@RequiredArgsConstructor
public class OrderService {

    // 공통코드 - 주문 상태
    private static final String ORD0001 = "ORD0001"; // 대기
    private static final String ORD0002 = "ORD0002"; // 완료
    private static final String ORD0003 = "ORD0003"; // 취소

    // private static final String PaymentService;
    // private static final String Stock;
    // private static final String DelieveryService;
 
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderStatusRepository orderStatusRepository;
    // private final StockRepository stockRepository;
    private final CommonCodeRepository commonCodeRepository;

    //Feign Clients
    private final StockClient stockClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @PersistenceContext
    private EntityManager em;

    private CommonCodeEntity code(String group, String c) {
        return commonCodeRepository.findByCodeGroupAndCode(group, c)
                .orElseThrow(() -> new IllegalArgumentException("invalid code: " + group + "/" + c));
    }

    // 1) 주문 생성: 재고 선차감(예약) + 주문(PENDING) -> 주문 완료 하면 
    // 재고 확인 먼저
    public OrderResponseDto create(OrderRequestDto req) {
        log.info("주문 생성 시작: customerId={}, productId={}", req.getCustomerId(), req.getProductId());

        //재고 확인
        Boolean stockAvailable = stockClient.checkStock(req.getProductId(), 1);
        if (!stockAvailable) {
            log.warn("재고 부족: productId={}", req.getProductId());
            throw new IllegalStateException("재고가 부족합니다.");
        }
        log.info("재고 확인 완료: productId={}", req.getProductId());

        // 주문 엔터티 생성
        OrderEntity order = orderRepository.save(
            OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(req.getCustomerId())
                .sellerId(req.getSellerId())
                // // .orderName(req.getOrderName())
                .totalAmount(req.getTotalAmount())
                // .orderDate(LocalDateTime.now())
                // .currentCode(ORD0001) // 초기 상태: 대기
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
        // 아이템 저장(단일 ProductId 기준)
        // prodictName: 우선 req.orderName 사용 (없으면 기본값)
        // 주문 상품 정보 저장
        String productName = (req.getOrderName() != null && !req.getOrderName().isBlank())
                ? req.getOrderName() : "상품";

        OrderProductEntity line = orderProductRepository.save(
            OrderProductEntity.builder()
            .id(UUID.randomUUID())
            .order(order)
            // .product(em.getReference(ProductEntity.class, req.getProductId())) //즉시 Select 없이 Fk만 물림
            .productId(req.getProductId()) //즉시 Select 없이 Fk만 물림
            .productName(productName)
            .quantity(1)
            .totalAmount(req.getTotalAmount())
            .createdAt(LocalDateTime.now())
            .build()
        );
        // 주문명 생성
        List<OrderProductEntity> lines = new ArrayList<>();
        lines.add(line);
        String orderName = OrderNameFormatter.makeOrderName(lines);

        order = order.toBuilder()
            .orderName(orderName)
            .updatedAt(LocalDateTime.now())
            .build();

        OrderEntity saved = orderRepository.save(order);

        //PENDING 상태
        appendOrderStatus(saved.getId(), ORD0001);
        log.info("주문 생성 완료: orderId={}, status=결제대기", saved.getId());

        OrderStatusEntity current = latestStatus(saved.getId());
        return OrderResponseDto.fromEntity(saved, current);
        // touchUpdatedAt(saved);

        //최신 상태 조회 후 DTO 반환
        // OrderStatusEntity current = orderStatusRepository
        //     .findTop1ByOrder_IdOrderByCreatedAtDesc(order.getId())
        //     .orElse(null);

        // return OrderResponseDto.fromEntity(order, current);
    }

    public void completePayment(UUID orderId, UUID paymentId) {
        // TODO: 구현 필요
    }

    public void completeReturn(UUID orderId, UUID paymentId) {
        // TODO: 구현 필요
    }

    public OrderResponseDto cancel(UUID orderId) {
        OrderEntity order = findOrderOrThrow(orderId);

        // OrderEntity order = findOrderOrThrow(orderId);

        // 결제 검증
        // Boolean paymentVerified = paymentClient.verifyPayment(
        //     new PaymentClient.PaymentVerifyRequest(orderId, paymentId, order.getTotalAmount())
        // );
        
        // if (!paymentVerified) {
        //     log.warn("결제 검증 실패: orderId={}, paymentId={}", orderId, paymentId);
        //     throw new IllegalStateException("결제 검증에 실패했습니다.");
        // }
        // log.info("결제 검증 완료: orderId={}", orderId);

        appendOrderStatus(orderId, ORD0001);
        
        OrderProductEntity product = orderProductRepository.findAll().stream()
            .filter(p -> p.getOrder().getId().equals(orderId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("주문 상품을 찾을 수 없습니다."));
        
        Boolean stockDecreased = stockClient.decreaseStock(product.getProductId(), product.getQuantity());
        if (!stockDecreased) {
            log.error("재고 차감 실패: orderId={}, productId={}", orderId, product.getProductId());
            throw new IllegalStateException("재고 차감에 실패했습니다.");
        }
        log.info("재고 차감 완료: orderId={}", orderId);

        //배송 요청
        Boolean deliveryRequested = deliveryClient.requestDelivery(
        new DeliveryClient.DeliveryRequest(
            orderId,
            order.getCustomerId(),
            "주소정보", // 실제로는 OrderRequestDto에 포함되어야 함
            "수령인",
            "010-0000-0000"
            )
        );
        if (!deliveryRequested) {
            log.error("배송 요청 실패: orderId={}", orderId);
            throw new IllegalStateException("배송 요청에 실패했습니다.");
        }
        log.info("배송 요청 완료: orderId={}", orderId);
        appendOrderStatus(orderId, ORD0001);
        touchUpdatedAt(order);

        OrderStatusEntity current = latestStatus(orderId);
        return OrderResponseDto.fromEntity(order, current);
    }
    //배송 상태 업데이트(완료 일 때만)
    @Transactional
    public OrderResponseDto updateDeliveryStatus(UUID orderId) {
        log.info("배송 상태 업데이트: orderId={}", orderId);

        OrderEntity order = findOrderOrThrow(orderId);

        DeliveryClient.DeliveryStatusResponse deliveryStatus = 
            deliveryClient.getDeliveryStatus(orderId);

        // DeliveryClient.DeliveryState state = deliveryClient.getState(orderId);
        // switch(state){
        //     case BEFORE_DELIVERY -> {
        //         //주문 취소 시퀀스 - 상품 발송 전
        //         paymentClient.cancelPayment(orderId);   // 3. 결제 취소 요청
        //         appendOrderStatus(orderId, ORD0003);    // 4. 상태 이력: 취소
        //         touchUpdatedAt(order);
        //         return OrderResponseDto.fromEntity(order);
        //         return OrderResposeDto.fromEntity(order, latestStatus(orderId); -> GPT는 이거 권장
        //     }
        //     case IN_TRANSIT, DELIVERED -> {
        //         // 주문 취소 시퀀스 - 배송 완료 이후 (또는 배송 중) → 반송 → 결제 취소
        //         deliveryClient.requestReturn(orderId);  // 3. 반송 요청 (비동기)
        //         // ❗주: 반송 완료 이벤트 수신 후에 payment.cancelPayment(orderId) 호출 → 최종 CANCELED 기록
        //         // 여기서는 즉시 취소 확정하지 않고, 반송 완료 이벤트 핸들러에서 최종 취소 처리 권장
        //         return OrderResposeDto.fromEntity(order, latestStatus(orderId);
        //     }
        //     case RETURNED -> {
        //         // 반송이 이미 완료되었다면 이제 결제 취소 후 취소 확정
        //         paymentClient.cancelPayment(orderId);
        //         appendOrderStatus(orderId, ORD0003);
        //         touchUpdatedAt(order);
        //         OrderStatusEntity current = orderStatusRepository // -> 이 부분 앞에 있음
        //         .findTop1ByOrder_IdOrderByCreatedAtDesc(orderId)
        //         .orElseThrow(() -> new IllegalStateException("상태 이력이 없습니다."));
        //         return OrderResposeDto.fromEntity(order, latestStatus(orderId);
        //     }
        
        // default -> throw new IllegalStateException(
        //     "배송 상태를 확인할 수 없습니다. orderId=" + orderId
        // ); // ✅ default에서도 반드시 return or throw
            return OrderResponseDto.fromEntity(order, latestStatus(orderId));
    }
    //배송 완료 시퀀스 딜리버리 콜백
    public OrderResponseDto completeDelivery(UUID orderId) {
        OrderEntity order = findOrderOrThrow(orderId);
    //     DelieveyClient.DelieveryState state = deliveryClient.getState(orderId);
    //     if (state == DelieveryClient.DeliveryState.BEFORE_SHIPMENT){
    //         throw new IllegalStateException("배송 중 단게에서는 주문 완료 불가")
    //     }
    //     appendOrderStatus(orderId, ORD0002);
    //     touchUpdatedAt(order);
        return OrderResponseDto.fromEntity(order, latestStatus(orderId));
    // return OrderResponseDto.fromEntity(order, latestStatus(orderId));
    }
        //단건 조회
        // @Transactional(readOnly = true)
        // public OrderResponseDto getOne(UUID id) {
        //     OrderEntity e = orderRepository.findById(id).orElseThrow();
        //     var current = orderStatusRepository.findTop1ByOrder_IdOrderByCreatedAtDesc(id)
        //             .orElseThrow(() -> new IllegalStateException("상태 이력이 없습니다."));
        //     return OrderDtoMapper.toResponseDto(e, current.getStatus().getCode(), current.getStatus().getCode());

    //단건 조회 최신
    @Transactional(readOnly = true)
    public OrderResponseDto getOne(UUID id) {
        OrderEntity e = findOrderOrThrow(id);
        OrderStatusEntity current = orderStatusRepository.findTop1ByOrder_IdOrderByCreatedAtDesc(id)
        .orElseThrow(() -> new IllegalStateException("상태 이력이 없습니다. orderId=" + id));
    // // return OrderDtoMapper.toResponseDto(e, current.getStatus().getCode(), current.getStatus().getCode());
        return OrderResponseDto.fromEntity(e, current);
    }
    //전체 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAll() {
        List<OrderEntity> entities = orderRepository.findAll();
        return entities.stream()
            .map(e -> {OrderStatusEntity current = orderStatusRepository
                .findTop1ByOrder_IdOrderByCreatedAtDesc(e.getId())
                .orElse(null);
                return OrderResponseDto.fromEntity(e, current);})
            .toList();
            // OrderResponseDto.fromEntity(e, latestStatus(e.getId())))
            // .toList();
    }
            // {
            //     var current = orderStatusRepository.findTop1ByOrder_IdOrderByCreatedAtDesc(e.getId())
            //             .orElseThrow(() -> new IllegalStateException("상태 이력이 없습니다."));
            //     return OrderDtoMapper.toResponseDto(e, current.getStatus().getCode(), current.getStatus().getCode());
            // }
            // ).collect(Collectors.toList());
        // }
    //상태 이력 추가
    private void appendOrderStatus(UUID orderId, String statusCodePk){
        OrderEntity order = findOrderOrThrow(orderId);
        // .orElseThrow(() -> new IllegalArgumentException("주문없음:" + orderId));

        CommonCodeEntity code = commonCodeRepository.findById(statusCodePk)
        .orElseThrow(() -> new IllegalArgumentException("코드없음:" + statusCodePk));
        LocalDateTime now = LocalDateTime.now();

        orderStatusRepository.save(
            OrderStatusEntity.builder()
                .id(UUID.randomUUID())
                .order(order)
                .status(code) // Fk: status_code + p_common_code.code
                .updatedAt(now)
                .build()
        );
        orderRepository.save(order.toBuilder().updatedAt(now).build());
    }
    //최신 상태 조회
    @Transactional(readOnly = true)
    private OrderStatusEntity latestStatus(UUID orderId){
        return orderStatusRepository.findTop1ByOrder_IdOrderByCreatedAtDesc(orderId)
            .orElseThrow(()-> new IllegalStateException("상태 이력이 없음. orderId=" + orderId));

    }
    //updatedAt 갱신
    private void touchUpdatedAt(OrderEntity order){
        orderRepository.save(order.toBuilder().updatedAt(LocalDateTime.now()).build());
    }
    // 내부 공통
    // private void appendOrderStatus(UUID orderId, String nextCode) {
    //     // OrderEntity order = orderRepository.findById(orderId).orElseThrow();
    //     OrderEntity order = findOrderOrThrow(orderID);
    //     var now = LocalDateTime.now();

    //     orderStatusRepository.save(
    //         OrderStatusEntity.builder()
    //             .id(UUID.randomUUID())
    //             .order(order)
    //             .status(code(ORDER_STATUS, nextCode))
    //             .createdAt(now)
    //             .build());
    //     orderRepository.save(order.toBuilder().updatedAt(now).build());
    // }
    //주문 조회
    private OrderEntity findOrderOrThrow(UUID id){
        return orderRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다. id=" + id));
    }






    // private void appendOrderStatus(UUID orderId, String nextCode) {
    // OrderEntity order = orderRepository.findById(orderId).orElseThrow();
    // var now = LocalDateTime.now();
    // orderStatusRepository.save(OrderStatusEntity.builder()
    //         .id(UUID.randomUUID())
    //         .order(order)
    //         .status(code(ORDER_STATUS, nextCode))
    //         .createdAt(now)
    //         .build());
    // orderRepository.save(order.toBuilder().updatedAt(now).build());
    // }


    //재고 조회 인데 이건 내가 안할듯

    // private void restoreStock(OrderEntity order) {
    //     StockEntity stock = stockRepository.findByProductIdForUpdate(order.getProductId())
    //             .orElseThrow(() -> new IllegalStateException("재고가 없습니다. productId=" + order.getProductId()));
    //     stock.increase(order.getQuantity());
    //     stockRepository.save(stock);
    // }
}