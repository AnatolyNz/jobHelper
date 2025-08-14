package mate.academy.dto;

import lombok.Data;
import mate.academy.model.JobApplicationStatus.Status;

@Data
public class JobApplicationUpdateDto {
    private Status status;
}
