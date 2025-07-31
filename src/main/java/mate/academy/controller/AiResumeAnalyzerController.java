package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AiAnalyzedResumeDto;
import mate.academy.service.AiResumeAnalyzerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai-resume-analysis")
@Tag(name = "AI Resume Analysis", description = "Analyze resumes using AI to detect tone, "
        + "grammar, structure, and optimization suggestions")
public class AiResumeAnalyzerController {

    private final AiResumeAnalyzerService analyzerService;

    @GetMapping("/{resumeId}")
    @Operation(
            summary = "Analyze Resume",
            description = "Performs AI-driven analysis on a resume and returns grammar feedback, "
                    + "tone suggestions, structure evaluation, and other optimization tips."
    )
    public ResponseEntity<?> analyze(@PathVariable Long resumeId) {
        AiAnalyzedResumeDto result = analyzerService.analyzeResume(resumeId);

        if (result == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Resume analysis failed: rule engine returned no result");
        }

        System.out.println("Returning analysis result for resumeId " + resumeId);
        return ResponseEntity.ok(result);
    }
}
