package profect.group1.goormdotcom.stock.controller.mapper;

import profect.group1.goormdotcom.stock.controller.dto.StockResponseDto;
import profect.group1.goormdotcom.stock.domain.Stock;

public class StockDtoMapper {
    
    public static StockResponseDto toStockResponseDto(Stock stock) {
        return new StockResponseDto(
            stock.getProductId(), 
            stock.getStockQuantity(), 
            stock.getUpdatedAt()
        );
    }
}
