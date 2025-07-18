package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.AiCustomizedResume;
import mate.academy.model.Job;
import mate.academy.model.Resume;
import mate.academy.repository.AiCustomizedResumeRepository;
import mate.academy.repository.JobRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.AiCustomizedResumeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiCustomizedResumeServiceImpl implements AiCustomizedResumeService {
    private final AiCustomizedResumeRepository customizedResumeRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    @Override
    public AiCustomizedResume generateCustomizedResume(Long resumeId, Long jobId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));

        AiCustomizedResume custom = new AiCustomizedResume();
        custom.setResume(resume);
        custom.setJob(job);
        custom.setContent("Tailored resume content for: " + job.getTitle());

        return customizedResumeRepository.save(custom);
    }
}
