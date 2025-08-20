package mate.academy.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Job;
import mate.academy.model.JobMatch;
import mate.academy.model.Resume;
import mate.academy.repository.JobMatchRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.JobMatchService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobMatchServiceImpl implements JobMatchService {
    private final JobMatchRepository jobMatchRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public List<JobMatch> findMatchesForResume(Long resumeId) {
        Resume resume = resumeRepository.findByIdWithSkillsAndUserRoles(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        resume.getExtractedSkills().size();

        System.out.println("Extracted skills: " + resume.getExtractedSkills());

        System.out.println("resume: " + resume);

        List<Job> jobs = jobMatchRepository.findAllWithRequiredSkills();

        return jobs.stream()
                .map(job -> {
                    double matchScore = calculateMatchScore(job, resume);

                    JobMatch match = new JobMatch();
                    match.setJob(job);
                    match.setResume(resume);
                    match.setMatchScore(matchScore);

                    return jobMatchRepository.save(match);
                })
                .collect(Collectors.toList());
    }

    private double calculateMatchScore(Job job, Resume resume) {

        System.out.println("resume.getSkills(): " + resume.getSkills());

        long matchingSkills = job.getRequiredSkills().stream()
                .filter(skill -> resume.getSkills().contains(skill))
                .count();

        double matchScore = (double) matchingSkills / job.getRequiredSkills().size() * 100;

        return matchScore;
    }
}
