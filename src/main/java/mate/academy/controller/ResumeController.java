package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.ResumeDto;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.service.ResumeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping
    @Operation(summary = "Create resume", description = "Allows users to add/upload a resume")
    public ResumeDto createResume(@RequestBody ResumeDto resumeDto) {
        Resume resume = resumeService.save(resumeDto);
        return resumeMapper.toDto(resume);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PostMapping("/{upload}")
    @Operation(summary = "Upload resume file", description
            = "Uploads a real resume file (PDF, DOCX) for a user")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file,
                                               @RequestParam("userId") Long userId) {
        Resume resume = resumeService.save(file, userId);
        System.out.println("test1");
        return ResponseEntity.ok("Resume uploaded with ID: " + resume.getId());
    }
}
