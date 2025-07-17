package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobApplicationDto;
import mate.academy.mapper.JobApplicationMapper;
import mate.academy.service.JobApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Job Application", description = "Track applied jobs and statuses")
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final JobApplicationMapper jobApplicationMapper;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's job applications", description =
            "Fetch job applications submitted by a user")
    public List<JobApplicationDto> getUserApplications(@PathVariable Long userId) {
        return jobApplicationService.findByUserId(userId)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }
}
