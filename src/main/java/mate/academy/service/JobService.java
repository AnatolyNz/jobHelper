package mate.academy.service;

import java.util.List;
import mate.academy.dto.JobDto;
import mate.academy.model.Job;

public interface JobService {
    Job save(JobDto jobDto);

    List<Job> findAll();

    Job findById(Long id);

    void delete(Long id);
}

