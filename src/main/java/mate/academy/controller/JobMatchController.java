package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.JobMatchDto;
import mate.academy.mapper.JobMatchMapper;
import mate.academy.service.JobMatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Job Match", description = "Endpoints for automatic job matching")
@RestController
@RequiredArgsConstructor
@RequestMapping("/job-matches")
public class JobMatchController {

    private final JobMatchService jobMatchService;
    private final JobMatchMapper jobMatchMapper;

    @GetMapping("/resume/{resumeId}")
    @Operation(summary = "Get job matches", description = "Find jobs that match a resume")
    public List<JobMatchDto> getMatches(@PathVariable Long resumeId) {
        return jobMatchService.findMatchesForResume(resumeId)
                .stream()
                .map(jobMatchMapper::toDto)
                .collect(Collectors.toList());
    }
}
