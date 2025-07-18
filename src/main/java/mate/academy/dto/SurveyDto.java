package mate.academy.dto;

import java.util.List;
import lombok.Data;

@Data
public class SurveyDto {
    private Long id;
    private Long userId;
    private String experienceLevel;
    private List<String> jobPreferences;
    private String locationPreference;
}
