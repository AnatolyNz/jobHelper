package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.ResumeDto;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.service.ResumeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resume", description = "Endpoints for managing resumes")
@RestController
@RequiredArgsConstructor
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeMapper resumeMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get resume", description = "Returns resume by ID")
    public ResumeDto getResume(@PathVariable Long id) {
        Resume resume = resumeService.findById(id);
        return resumeMapper.toDto(resume);
    }
}
