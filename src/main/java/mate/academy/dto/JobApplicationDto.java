package mate.academy.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class JobApplicationDto {
    private Long id;
    private Long userId;
    private Long jobId;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String company;
    private String location;
    private BigDecimal salary;
    private String workFormat;
    private String status;
}
