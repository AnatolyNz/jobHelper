package mate.academy.dto;

import java.util.List;
import lombok.Data;

@Data
public class SurveyResponseDto {
    private Long id;
    private String experienceLevel;
    private List<String> jobPreferences;
    private String locationPreference;
}
