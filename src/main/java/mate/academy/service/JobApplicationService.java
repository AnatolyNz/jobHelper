package mate.academy.service;

import java.util.List;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.model.JobApplication;
import mate.academy.model.JobApplicationStatus.Status;

public interface JobApplicationService {
    List<JobApplication> findByUserId(Long userId);

    JobApplication findById(Long id);

    JobApplication create(JobApplicationRequestDto dto);

    JobApplication updateStatus(Long id, Status status);

    void delete(Long id);
}
