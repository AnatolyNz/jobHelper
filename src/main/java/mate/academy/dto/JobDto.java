package mate.academy.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class JobDto {
    private Long id;
    private String title;
    private String description;
    private List<String> requiredSkills = new ArrayList<>();
    private String company;
    private String location;
    private BigDecimal salary;
    private String workFormat;
}
