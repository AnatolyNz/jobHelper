package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.dto.AiCustomizedResumeDto;
import mate.academy.model.AiCustomizedResume;
import mate.academy.service.AiCustomizedResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ai Resume Customizer", description = "AI service to tailor resume for a job")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai-custom-resume")
public class AiCustomizedResumeController {

    private final AiCustomizedResumeService aiCustomizedResumeService;

    @PostMapping("/{resumeId}/job/{jobId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Generate AI-customized resume",
            description = "Uses AI to tailor resume for a job")
    public ResponseEntity<AiCustomizedResumeDto> generate(@PathVariable Long resumeId,
                                                          @PathVariable Long jobId) {
        AiCustomizedResume resume = aiCustomizedResumeService
                .generateCustomizedResume(resumeId, jobId);
        log.info("AiCustomizedResumeController.generate() was called!");
        AiCustomizedResumeDto dto = new AiCustomizedResumeDto(
                resume.getId(),
                resume.getResume().getId(),
                resume.getJob().getId(),
                resume.getContent(),
                resume.getGeneratedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
