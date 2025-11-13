package mate.academy.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.model.Job;
import mate.academy.model.JobApplication;
import mate.academy.model.JobApplicationStatus;
import mate.academy.model.JobApplicationStatus.Status;
import mate.academy.model.User;
import mate.academy.repository.JobApplicationRepository;
import mate.academy.repository.JobApplicationStatusRepository;
import mate.academy.repository.JobRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.JobApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationStatusRepository jobApplicationStatusRepository;

    @Override
    public List<JobApplication> findByUserId(Long userId) {
        return jobApplicationRepository.findByUserIdWithDetails(userId);
    }

    @Override
    public JobApplication findById(Long id) {
        JobApplication application = jobApplicationRepository.findByIdWithDetails(id);
        if (application == null) {
            throw new NoSuchElementException("Job application not found: " + id);
        }
        return application;
    }

    @Override
    @Transactional
    public JobApplication create(JobApplicationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: "
                        + requestDto.getUserId()));

        Job job = jobRepository.findByIdWithRequiredSkills(requestDto.getJobId())
                .orElseThrow(() -> new NoSuchElementException("Job not found: "
                        + requestDto.getJobId()));

        JobApplication application = new JobApplication();
        application.setUser(user);
        application.setJob(job);

        JobApplicationStatus status = new JobApplicationStatus();
        status.setStatus(Status.PENDING);
        status.setJobApplication(application);

        application.setStatus(status);

        jobApplicationRepository.save(application);
        jobApplicationStatusRepository.save(status);

        return application;
    }

    @Override
    @Transactional
    public JobApplication updateStatus(Long id, Status newStatus) {
        JobApplication application = jobApplicationRepository.findByIdWithDetails(id);
        if (application == null) {
            throw new NoSuchElementException("Job application not found: " + id);
        }

        application.getStatus().setStatus(newStatus);
        jobApplicationStatusRepository.save(application.getStatus());
        return application;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new NoSuchElementException("Job application not found: " + id);
        }
        jobApplicationRepository.deleteById(id);
    }
}
