package mate.academy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApplicationRequestDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long jobId;
}
