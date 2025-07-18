package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.model.JobApplication;
import mate.academy.repository.JobApplicationRepository;
import mate.academy.service.JobApplicationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;

    @Override
    public List<JobApplication> findByUserId(Long userId) {
        return jobApplicationRepository.findByUserId(userId);
    }
}
