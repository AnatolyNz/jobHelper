package mate.academy.dto;

import java.util.List;
import lombok.Data;

@Data
public class ResumeDto {
    private Long id;
    private Long userId;
    private String fileUrl;
    private String originalFileName;
    private List<String> extractedSkills;
}
