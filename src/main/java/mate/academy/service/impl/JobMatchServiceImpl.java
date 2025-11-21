package mate.academy.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobMatchDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Job;
import mate.academy.model.Resume;
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

        Set<String> resumeSkillNames = resume.getSkills().stream()
                .map(skill -> skill.getName().toLowerCase())
                .collect(Collectors.toSet());

        if (jobId != null) {
            Job job = jobRepository.findByIdWithRequiredSkills(jobId)
                    .orElseThrow(() -> new EntityNotFoundException("Job not found: " + jobId));
            return List.of(calculateJobMatch(resume.getId(), resumeSkillNames, job));
        }

        List<Job> jobs = findAllJobs();
        return jobs.stream()
                .map(job -> calculateJobMatch(resume.getId(), resumeSkillNames, job))
                .toList();
    }

    /**
     * Безпечне отримання всіх вакансій через пагінацію
     */
    private List<Job> findAllJobs() {
        int page = 0;
        List<Job> allJobs = new java.util.ArrayList<>();
        Page<Job> jobPage;
        do {
            jobPage = jobRepository.findAllWithRequiredSkills(PageRequest.of(page++, PAGE_SIZE));
            allJobs.addAll(jobPage.getContent());
        } while (!jobPage.isEmpty());
        return allJobs;
    }

    private JobMatchDto calculateJobMatch(Long resumeId, Set<String> resumeSkillNames, Job job) {
        long matchedCount = job.getRequiredSkills().stream()
                .map(skill -> skill.getName().toLowerCase())
                .filter(resumeSkillNames::contains)
                .count();

        double matchScore = job.getRequiredSkills().isEmpty()
                ? 0.0
                : (matchedCount * 100.0 / job.getRequiredSkills().size());

        JobMatchDto dto = new JobMatchDto();
        dto.setResumeId(resumeId);
        dto.setJobId(job.getId());
        dto.setJobTitle(job.getTitle());
        dto.setMatchScore(matchScore);
        return dto;
    }
}
