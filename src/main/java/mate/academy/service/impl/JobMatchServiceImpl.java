package mate.academy.service.impl;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobMatchDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Job;
import mate.academy.model.Resume;
import mate.academy.model.Skill;
import mate.academy.repository.JobRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.JobMatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobMatchServiceImpl implements JobMatchService {

    private static final int PAGE_SIZE = 50;

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobMatchDto> findMatchesForResume(Long resumeId, Long jobId) {
        Resume resume = resumeRepository.findByIdWithSkills(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        if (jobId != null) {
            Job job = jobRepository.findByIdWithRequiredSkills(jobId)
                    .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));
            return List.of(calculateJobMatch(resume, job));
        }

        try (Stream<Job> jobStream = streamAllJobs()) {
            return jobStream.map(job -> calculateJobMatch(resume, job))
                    .toList(); // Java 16+, use Collectors.toList() if older
        }
    }

    private Stream<Job> streamAllJobs() {
        return Stream.iterate(0, page -> page + 1)
                .map(page -> jobRepository.findAllWithRequiredSkills(PageRequest
                        .of(page, PAGE_SIZE)))
                .takeWhile(page -> !page.isEmpty())
                .flatMap(Page::stream);
    }

    private JobMatchDto calculateJobMatch(Resume resume, Job job) {
        List<Skill> resumeSkills = resume.getSkills().stream().toList();
        List<Skill> requiredSkills = job.getRequiredSkills();

        long matched = resumeSkills.stream()
                .filter(rs -> requiredSkills.stream()
                        .anyMatch(js -> js.getName().equalsIgnoreCase(rs.getName())))
                .count();

        double matchScore = requiredSkills.isEmpty() ? 0.0 : (matched * 100.0
                / requiredSkills.size());

        JobMatchDto dto = new JobMatchDto();
        dto.setJobId(job.getId());
        dto.setJobTitle(job.getTitle());
        dto.setResumeId(resume.getId());
        dto.setMatchScore(matchScore);
        return dto;
    }
}
