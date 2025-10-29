package profect.group1.goormdotcom.review.controller.external.v1.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * 상품별 리뷰 목록 응답 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "페이지네이션 정보")
public class ProductReviewListResponseDto {
    @Schema(description = "상품 ID")
    private UUID productId;
    @Schema(description = "평균 별점")
    private Double averageRating;
    @Schema(description = "총 리뷰 개수")
    private Integer totalReviews;
    @Schema(description = "리뷰 목록")
    private List<ReviewResponseDto> reviews;  // 리뷰 목록

    // 페이징 정보
    @Schema(description = "현재 페이지")
    private int currentPage;

    @Schema(description = "페이지 당 데이터 개수")
    private int pageSize;

    @Schema(description = "전체 데이터 개수")
    private long totalElements;

    @Schema(description = "전체 페이지 수")
    private int totalPages;
}
