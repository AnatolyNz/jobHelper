package mate.academy.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class JobApplicationDto {
    private Long id;
    private Long userId;
    private Long jobId;
    private String jobTitle;
    private String company;
    private String status; // Enum suggested: PENDING, ACCEPTED, REJECTED, etc.
    private LocalDateTime appliedAt;
}
