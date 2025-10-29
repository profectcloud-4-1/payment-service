package profect.group1.goormdotcom.stock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import profect.group1.goormdotcom.stock.repository.StockRepository;
import profect.group1.goormdotcom.stock.repository.entity.StockEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class AdjustStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void tryDecreaseStocks(Map<UUID, Integer> requestedQuantityMap) {
        for (UUID productId: requestedQuantityMap.keySet()) {
            Optional<StockEntity> stockEntity = stockRepository.findByProductId(productId);

            if (stockEntity.isEmpty()) {
                throw new IllegalArgumentException("Product not found");
            }
            StockEntity entity = stockEntity.get();
            int requestedStockQuantity = requestedQuantityMap.get(productId);
            entity.decreaseQuantity(requestedStockQuantity);
            stockRepository.save(entity);
        }
    }

    @Transactional
    public void tryIncreaseStocks(Map<UUID, Integer> requestedQuantityMap) {
        for (UUID productId: requestedQuantityMap.keySet()) {
            Optional<StockEntity> stockEntity = stockRepository.findByProductId(productId);

            if (stockEntity.isEmpty()) {
                throw new IllegalArgumentException("Product not found");
            }
            StockEntity entity = stockEntity.get();
            int requestedStockQuantity = requestedQuantityMap.get(productId);
            entity.increaseQuantity(requestedStockQuantity);
            stockRepository.save(entity);
        }
    }
}