package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobDto;
import mate.academy.mapper.JobMapper;
import mate.academy.model.Job;
import mate.academy.repository.JobRepository;
import mate.academy.service.JobService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;

    @Override
    public Job save(JobDto jobDto) {
        Job job = jobMapper.toModel(jobDto);
        return jobRepository.save(job);
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    @Override
    public Job findById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        jobRepository.deleteById(id);
    }
}
