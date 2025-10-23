package profect.group1.goormdotcom.stock.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import profect.group1.goormdotcom.common.domain.BaseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_stock")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
@SQLDelete(sql = "update p_stock set deleted_at = NOw() WHERE id = ?")
public class StockEntity extends BaseEntity{
    
    @Id
    private UUID id;
    
    @Column(unique=true)
    private UUID productId;
    private int stockQuantity;
    private LocalDateTime deletedAt;

    public StockEntity(
        UUID id,
        UUID productId,
        int stockQuantity
    ) {
        this.id = id;
        this.productId = productId;
        this.stockQuantity = stockQuantity;
    }

    public void updateQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void decreaseStock(int requestedQuantity) {
        if (this.stockQuantity - requestedQuantity < 0) {
            throw new IllegalArgumentException("재고가 요청한 수량보다 부족합니다.");
        }

        this.stockQuantity -= requestedQuantity;
    }

    public void increaseStock(int requestedQuantity) {
        this.stockQuantity += requestedQuantity;
    }

}
