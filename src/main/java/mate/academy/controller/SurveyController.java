package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.SurveyDto;
import mate.academy.dto.SurveyRequestDto;
import mate.academy.mapper.SurveyMapper;
import mate.academy.model.Survey;
import mate.academy.service.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Survey", description = "Endpoints for onboarding surveys")
@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyMapper surveyMapper;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create onboarding survey", description
            = "Captures job preferences and experience")
    public SurveyDto createSurvey(@RequestBody @Valid SurveyRequestDto request) {
        Survey survey = surveyService.save(request);
        return surveyMapper.toDto(survey);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get survey by ID", description = "Returns onboarding survey for user")
    public SurveyDto getSurvey(@PathVariable Long id) {
        return surveyMapper.toDto(surveyService.findById(id));
    }
}
