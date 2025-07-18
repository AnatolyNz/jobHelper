package mate.academy.service;

import java.util.List;
import mate.academy.model.JobApplication;

public interface JobApplicationService {
    List<JobApplication> findByUserId(Long userId);
}
