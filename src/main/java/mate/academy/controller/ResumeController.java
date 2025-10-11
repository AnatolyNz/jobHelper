package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.ResumeDto;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.service.ResumeParserService;
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

    private final ResumeParserService resumeParserService;
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
    @Operation(summary = "Upload resume file", description =
            "Uploads a real resume (PDF, DOCX) for a user")
    public ResponseEntity<Map<String, Object>> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            byte[] fileData = file.getBytes();
            String extractedText = resumeParserService.extractTextFromFileData(fileData);
            List<String> extractedSkills = resumeParserService.extractSkills(extractedText);

            ResumeDto resumeDto = new ResumeDto();
            resumeDto.setFileName(file.getOriginalFilename());
            resumeDto.setFileType(file.getContentType());
            resumeDto.setFileData(fileData);
            resumeDto.setUserId(userId);
            resumeDto.setExtractedSkills(extractedSkills);

            Resume savedResume = resumeService.save(resumeDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Resume uploaded successfully");
            response.put("resumeId", savedResume.getId());
            response.put("skillsExtracted", extractedSkills);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to upload resume");
            error.put("details", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
