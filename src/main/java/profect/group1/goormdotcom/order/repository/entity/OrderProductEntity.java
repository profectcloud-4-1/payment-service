package profect.group1.goormdotcom.order.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import java.util.List;   

@Entity
@Table(name = "p_order_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class OrderProductEntity {

    @Id
    @GeneratedValue
    private UUID id;

    // 주문 정보
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    //상품 정보
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private UUID productId;


    // private UUID productId;

    @Column(name = "order_name", nullable=false)
    private String productName;
    
    //단일 상품 수량  
    @Column(name="quantity", nullable=false)
    private int quantity;

    @Column(name="total_amount", nullable=false)
    private int totalAmount;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
}
