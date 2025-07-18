package mate.academy.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Job;
import mate.academy.model.JobMatch;
import mate.academy.model.Resume;
import mate.academy.repository.JobMatchRepository;
import mate.academy.repository.JobRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.JobMatchService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobMatchServiceImpl implements JobMatchService {
    private final JobMatchRepository jobMatchRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    @Override
    public List<JobMatch> findMatchesForResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(job -> {
                    JobMatch match = new JobMatch();
                    match.setJob(job);
                    match.setResume(resume);
                    match.setMatchScore(75); // Example static score
                    return jobMatchRepository.save(match);
                }).collect(Collectors.toList());
    }
}
