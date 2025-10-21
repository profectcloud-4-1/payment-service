package profect.group1.goormdotcom.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApproveRequestDto {
    @Schema(description = "승인 여부")
    @NotNull
    private boolean approval;
}
