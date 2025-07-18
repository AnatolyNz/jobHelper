package mate.academy.dto;

import lombok.Data;

@Data
public class AtsEvaluationDto {
    private Long id;
    private Long resumeId;
    private double score;
    private String feedback;
}
