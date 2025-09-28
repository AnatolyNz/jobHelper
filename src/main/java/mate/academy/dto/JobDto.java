package mate.academy.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String description;
    private List<String> requiredSkills;
    private String company;
    private String location;
    private BigDecimal salary;
    private String workFormat;
}
