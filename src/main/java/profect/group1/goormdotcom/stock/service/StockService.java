package profect.group1.goormdotcom.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import profect.group1.goormdotcom.stock.domain.Stock;
import profect.group1.goormdotcom.stock.repository.StockRepository;
import profect.group1.goormdotcom.stock.repository.entity.StockEntity;
import profect.group1.goormdotcom.stock.repository.mapper.StockMapper;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final StockRepository stockRepository;

    private StockEntity getStockEntity(UUID productId) {
        Optional<StockEntity> stockEntity = stockRepository.findByProductId(productId);
        
        if (stockEntity.isEmpty()) {
			throw new IllegalArgumentException("Product not found");
		}
        return stockEntity.get(); 
    }

    @Transactional
    public Stock registerStock(UUID productId, int stockQuantity) {
        stockRepository.findByProductId(productId)
            .ifPresent(stock -> { throw new IllegalArgumentException("Stock already exists");});

        UUID id = UUID.randomUUID();
        StockEntity entity = new StockEntity(id, productId, stockQuantity);
        stockRepository.save(entity);
        return StockMapper.toDomain(entity);
    }
    
    @Transactional
    public Stock updateStock(UUID productId, int stockQuantity) {
        StockEntity entity = getStockEntity(productId);
        entity.updateQuantity(stockQuantity);
        stockRepository.save(entity);
        return StockMapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public Stock getStock(UUID productId) {
        StockEntity entity = getStockEntity(productId);
        return StockMapper.toDomain(entity);
    }

    @Transactional
    public Stock deleteStock(UUID productId) {
        StockEntity entity = stockRepository.deleteByProductId(productId);
        return StockMapper.toDomain(entity);
    }

    @Transactional
    public void decreaseStocks(Map<UUID, Integer> requestedQuantityMap) {
        StockEntity entity;
        for (UUID productId: requestedQuantityMap.keySet()) {
            entity = getStockEntity(productId);
            entity.decreaseQuantity(requestedQuantityMap.get(productId));
            
            stockRepository.save(entity);
        }
    }

    @Transactional
    public void increaseStocks(Map<UUID, Integer> requestedQuantityMap) {
        StockEntity entity;
        for (UUID productId: requestedQuantityMap.keySet()) {
            entity = getStockEntity(productId);
            entity.increaseQuantity(requestedQuantityMap.get(productId));
            
            stockRepository.save(entity);   
        }
    }
}
