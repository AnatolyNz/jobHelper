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
        return jobApplicationRepository.findByUserId(userId);
    }

    @Override
    public JobApplication findById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job application not found: " + id));
    }

    @Override
    @Transactional
    public JobApplication create(JobApplicationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: "
                        + requestDto.getUserId()));

        Job job = jobRepository.findById(requestDto.getJobId())
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
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job application not found: " + id));

        JobApplicationStatus currentStatus = application.getStatus();
        currentStatus.setStatus(newStatus);

        jobApplicationStatusRepository.save(currentStatus);
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
