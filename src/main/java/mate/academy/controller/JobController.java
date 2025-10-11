package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobDto;
import mate.academy.mapper.JobMapper;
import mate.academy.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Job", description = "Endpoints for job listings")
@RestController
@RequiredArgsConstructor
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;
    private final JobMapper jobMapper;

    @GetMapping
    @Operation(summary = "List all jobs", description = "Returns list of all job postings")
    public List<JobDto> getAllJobs() {
        return jobService.findAll()
                .stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID", description = "Returns job details by ID")
    public JobDto getJob(@PathVariable Long id) {
        return jobMapper.toDto(jobService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new job", description = "Create a new job posting")
    public JobDto createJob(@RequestBody JobDto jobDto) {
        return jobMapper.toDto(jobService.save(jobDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update job", description = "Update an existing job posting by ID")
    public JobDto updateJob(@PathVariable Long id, @RequestBody JobDto jobDto) {
        return jobMapper.toDto(jobService.update(id, jobDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete job", description = "Deletes a job by ID")
    public void deleteJob(@PathVariable Long id) {
        jobService.delete(id);
    }
}
