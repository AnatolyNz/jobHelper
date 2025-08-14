package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobApplicationDto;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.dto.JobApplicationUpdateDto;
import mate.academy.mapper.JobApplicationMapper;
import mate.academy.model.JobApplication;
import mate.academy.service.JobApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Job Application", description = "Track applied jobs and statuses")
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final JobApplicationMapper jobApplicationMapper;

    @Operation(summary = "Get all job applications for a user")
    @GetMapping("/user/{userId}")
    public List<JobApplicationDto> getUserApplications(@PathVariable Long userId) {
        return jobApplicationService.findByUserId(userId)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get job application by ID")
    @GetMapping("/{id}")
    public JobApplicationDto getById(@PathVariable Long id) {
        return jobApplicationMapper.toDto(jobApplicationService.findById(id));
    }

    @Operation(summary = "Create a new job application")
    @PostMapping
    public ResponseEntity<JobApplicationDto> create(@RequestBody
                                                        JobApplicationRequestDto requestDto) {
        JobApplication jobApplication = jobApplicationService.create(requestDto);
        return new ResponseEntity<>(jobApplicationMapper
                .toDto(jobApplication), HttpStatus.CREATED);
    }

    @Operation(summary = "Update job application status")
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDto> updateStatus(
            @PathVariable Long id,
            @RequestBody JobApplicationUpdateDto updateDto) {
        JobApplication updated = jobApplicationService.updateStatus(id, updateDto.getStatus());
        return ResponseEntity.ok(jobApplicationMapper.toDto(updated));
    }

    @Operation(summary = "Delete a job application")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
