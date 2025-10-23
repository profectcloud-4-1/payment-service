package profect.group1.goormdotcom.stock.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import profect.group1.goormdotcom.apiPayload.ApiResponse;
import profect.group1.goormdotcom.stock.controller.dto.ChangeStockQuantityRequestDto;
import profect.group1.goormdotcom.stock.controller.dto.ChangeStockQuantityResponseDto;
import profect.group1.goormdotcom.stock.controller.dto.StockRequestDto;
import profect.group1.goormdotcom.stock.controller.dto.StockResponseDto;

@Tag(name = "재고", description = "재고 관리 API")
public interface StockApiDocs {

    @Operation(summary = "재고 등록", description = "상품의 초기 재고를 등록합니다.", security = { @SecurityRequirement(name = "bearerAuth") })
    ApiResponse<StockResponseDto> registerStock(
        @Parameter(description = "재고 등록 요청", required = true)
        @RequestBody StockRequestDto stockRequestDto
    );

    @Operation(summary = "재고 수정", description = "상품의 재고 수량을 수정합니다.", security = { @SecurityRequirement(name = "bearerAuth") })
    ApiResponse<StockResponseDto> updateStock(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable(value = "productId") UUID productId,
        @Parameter(description = "수정할 재고 수량", required = true)
        @RequestBody int stockQuantity
    );

    @Operation(summary = "재고 조회", description = "상품의 현재 재고를 조회합니다.")
    ApiResponse<StockResponseDto> getStock(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable(value = "productId") UUID productId
    );

    @Operation(summary = "재고 삭제", description = "상품의 재고 정보를 삭제합니다.")
    ApiResponse<UUID> deleteStock(
        @Parameter(description = "상품 ID", required = true)
        @PathVariable(value = "productId") UUID productId
    );

    @Operation(summary = "재고 차감", description = "주문 시 요청 수량만큼 재고를 차감합니다.")
    ApiResponse<ChangeStockQuantityResponseDto> checkStock(
        @Parameter(description = "재고 차감 요청", required = true)
        @RequestBody ChangeStockQuantityRequestDto changeStockQuantityRequestDto
    );

    @Operation(summary = "재고 증가", description = "반품/취소 등으로 재고를 증가시킵니다.")
    ApiResponse<ChangeStockQuantityResponseDto> increaseStock(
        @Parameter(description = "재고 증가 요청", required = true)
        @RequestBody ChangeStockQuantityRequestDto changeStockQuantityRequestDto
    );
}

