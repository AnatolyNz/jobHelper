package mate.academy.service;

import java.util.List;
import mate.academy.dto.JobDto;
import mate.academy.model.Job;

public interface JobService {
    Job save(JobDto jobDto);

    List<Job> findAll();

    Job findById(Long id);

    Job update(Long id, JobDto jobDto);

    void delete(Long id);
}

