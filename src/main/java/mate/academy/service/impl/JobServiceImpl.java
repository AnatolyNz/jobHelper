package mate.academy.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobDto;
import mate.academy.mapper.JobMapper;
import mate.academy.model.Job;
import mate.academy.model.Skill;
import mate.academy.repository.JobRepository;
import mate.academy.repository.SkillRepository;
import mate.academy.service.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    private final SkillRepository skillRepository;

    @Override
    public Job save(JobDto jobDto) {
        Job job = jobMapper.toModel(jobDto);

        if (job.getRequiredSkills() == null) {
            job.setRequiredSkills(List.of());
        }

        List<Skill> skills = jobDto.getRequiredSkills().stream()
                .map(name -> skillRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> skillRepository.save(new Skill(name))))
                .collect(Collectors.toList());

        job.setRequiredSkills(skills);

        if (job.getJobApplications() == null) {
            job.setJobApplications(List.of());
        }

        if (job.getJobMatches() == null) {
            job.setJobMatches(List.of());
        }

        return jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Job> findAll() {
        List<Job> jobs = jobRepository.findAll();

        jobs.forEach(job -> {
            job.getRequiredSkills().size();
            job.getJobApplications().size();
            job.getJobMatches().size();
        });

        return jobs;
    }

    @Override
    @Transactional(readOnly = true)
    public Job findById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        job.getRequiredSkills().size();
        job.getJobApplications().size();
        job.getJobMatches().size();

        return job;
    }

    @Override
    public void delete(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public Job update(Long id, JobDto jobDto) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        existingJob.setTitle(jobDto.getTitle());
        existingJob.setDescription(jobDto.getDescription());
        existingJob.setCompany(jobDto.getCompany());
        existingJob.setLocation(jobDto.getLocation());
        existingJob.setSalary(jobDto.getSalary());

        if (jobDto.getWorkFormat() != null) {
            try {
                existingJob.setWorkFormat(Job.WorkFormat.valueOf(jobDto.getWorkFormat()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid work format: " + jobDto.getWorkFormat()
                        + ". Valid options: " + Arrays.toString(Job.WorkFormat.values()));
            }
        }

        if (jobDto.getRequiredSkills() != null && !jobDto.getRequiredSkills().isEmpty()) {
            List<Skill> skills = jobDto.getRequiredSkills().stream()
                    .map(name -> skillRepository.findByNameIgnoreCase(name)
                            .orElseGet(() -> skillRepository.save(new Skill(name))))
                    .collect(Collectors.toList());
            existingJob.setRequiredSkills(skills);
        }

        return jobRepository.save(existingJob);
    }
}
