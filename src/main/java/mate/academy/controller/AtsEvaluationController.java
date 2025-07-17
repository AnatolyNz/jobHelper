package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AtsEvaluationDto;
import mate.academy.mapper.AtsEvaluationMapper;
import mate.academy.model.AtsEvaluation;
import mate.academy.service.AtsEvaluationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ATS Evaluation", description = "AI/ATS resume scoring endpoints")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ats-evaluation")
public class AtsEvaluationController {

    private final AtsEvaluationService atsEvaluationService;
    private final AtsEvaluationMapper atsEvaluationMapper;

    @GetMapping("/resume/{resumeId}")
    @Operation(summary = "Evaluate resume", description = "Returns ATS evaluation for a resume")
    public AtsEvaluationDto evaluate(@PathVariable Long resumeId) {
        AtsEvaluation evaluation = atsEvaluationService.evaluateResume(resumeId);
        return atsEvaluationMapper.toDto(evaluation);
    }
}
