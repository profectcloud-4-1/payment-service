package profect.group1.goormdotcom.review.controller.external.v1.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data

@Getter
@AllArgsConstructor
@Schema(description = "리뷰 정보")
public class ReviewResponseDto {
    @Schema(description = "리뷰 고유 ID")
    private UUID reviewId;

    @Schema(description = "별점 (1~5)")
    private int rating;

    @Schema(description = "작성자 유저 ID")
    private UUID userId;

    @Schema(description = "리뷰 내용")
    private String content;

    @Schema(description = "리뷰 이미지 URL")
    private String imageUrl;

    @Schema(description = "리뷰 작성 일시")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // POST/PUT/GET(항목별로) 모두 이 Response 재사용

}
