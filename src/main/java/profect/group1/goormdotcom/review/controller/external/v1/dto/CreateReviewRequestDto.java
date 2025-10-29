package profect.group1.goormdotcom.review.controller.external.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateReviewRequestDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    @Schema(description = "상품 ID")
    private UUID productId;

    @NotNull(message = "별점은 필수입니다.")
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
    @Schema(description = "별점")
    private int rating;

    @NotNull(message = "내용은 필수입니다.")
    @Size(min = 10, max = 500, message = "리뷰는 10자 이상 500자 이하로 작성해주세요.")
    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "파일 ID")
    private UUID fileId;


}
