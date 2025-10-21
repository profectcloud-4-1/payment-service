package profect.group1.goormdotcom.common.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestDto {
    @Schema(description = "페이지 번호", defaultValue = "1")
    private int page = 1;
    @Schema(description = "페이지 크기", defaultValue = "10")
    private int size = 10;
    @Schema(description = "정렬 필드", defaultValue = "createdAt")
    private String sort = "createdAt";
    @Schema(description = "정렬 순서", defaultValue = "desc")
    private String order = "desc";
    @Schema(description = "검색 키워드")
    private String keyword = "";

    public void setSize(int size) {
        if (size == 10 || size == 30 || size == 50) {
            this.size = size;
        } else {
            this.size = 10;
        }
    }

    public void setPage(int page) {
        this.page = page > 0 ? page : 1;
    }

    public void setSort(String sort) {
        this.sort = (sort == null || sort.isBlank()) ? "createdAt" : sort;
    }

    public void setOrder(String order) {
        this.order = (order == null || order.isBlank()) ? "desc" : order;
    }

    public void setKeyword(String keyword) {
        this.keyword = (keyword == null) ? "" : keyword;
    }
}