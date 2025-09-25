package mate.academy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;
}
