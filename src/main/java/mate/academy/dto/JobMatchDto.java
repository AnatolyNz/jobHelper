package mate.academy.dto;

import lombok.Data;

@Data
public class JobMatchDto {
    private Long resumeId;
    private Long jobId;
    private String jobTitle;
    private double matchScore;
}
