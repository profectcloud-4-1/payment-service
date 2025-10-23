package profect.group1.goormdotcom.stock.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import profect.group1.goormdotcom.stock.controller.dto.ChangeStockQuantityRequestDto;
import profect.group1.goormdotcom.stock.controller.dto.ChangeStockQuantityResponseDto;
import profect.group1.goormdotcom.stock.controller.dto.StockRequestDto;
import profect.group1.goormdotcom.stock.controller.dto.StockResponseDto;
import profect.group1.goormdotcom.stock.controller.mapper.StockDtoMapper;
import profect.group1.goormdotcom.stock.domain.Stock;
import profect.group1.goormdotcom.stock.repository.StockRepository;
import profect.group1.goormdotcom.stock.service.StockService;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.apiPayload.code.status.SuccessStatus;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
public class StockController implements StockApiDocs {

    private final StockService stockService;
    private final StockRepository stockRepository;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<StockResponseDto> registerStock(
        @RequestBody StockRequestDto stockRequestDto
    ) {
        Stock stock = stockService.registerStock(stockRequestDto.productId(), stockRequestDto.stockQuantity());
        return ApiResponse.of(SuccessStatus._OK, StockDtoMapper.toStockResponseDto(stock));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<StockResponseDto> updateStock(
        @PathVariable(value="productId") UUID productId, 
        @RequestBody int stockQuantity
    ) {
        Stock stock = stockService.updateStock(productId, stockQuantity);
        return ApiResponse.of(SuccessStatus._OK, StockDtoMapper.toStockResponseDto(stock));
    }

    @GetMapping("/{productId}")
    public ApiResponse<StockResponseDto> getStock(
        @PathVariable(value = "productId") UUID productId
    ) {
        
        Stock stock = stockService.getStock(productId);
        return ApiResponse.of(SuccessStatus._OK, StockDtoMapper.toStockResponseDto(stock));        
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<UUID> deleteStock(
        @PathVariable(value = "productId") UUID productId
    ) {
        stockService.deleteStock(productId);
        return ApiResponse.of(SuccessStatus._OK, productId);
    }
    
    @PostMapping("/check")
    public ApiResponse<ChangeStockQuantityResponseDto> checkStock(
        @RequestBody ChangeStockQuantityRequestDto changeStockQuantityRequestDto
    ) {
        Boolean status = stockService.decreaseStock(
            changeStockQuantityRequestDto.productId(),
            changeStockQuantityRequestDto.requestedStockQuantity()
        );
        
        return ApiResponse.of(
            SuccessStatus._OK, 
            new ChangeStockQuantityResponseDto(status, changeStockQuantityRequestDto.productId())
        );
    }

    @PostMapping("/increase")
    public ApiResponse<ChangeStockQuantityResponseDto> increaseStock(
        @RequestBody ChangeStockQuantityRequestDto changeStockQuantityRequestDto
    ) {
        Boolean status = stockService.increaseStock(
            changeStockQuantityRequestDto.productId(),
            changeStockQuantityRequestDto.requestedStockQuantity()
        );
        
        return ApiResponse.of(
            SuccessStatus._OK, 
            new ChangeStockQuantityResponseDto(status, changeStockQuantityRequestDto.productId())
        );
    }
    
}
