package mate.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class SurveyRequestDto {

    @NotNull
    private Long userId;

    @NotBlank
    private String experienceLevel;

    @NotNull
    private List<String> jobPreferences;

    private String locationPreference;
}
