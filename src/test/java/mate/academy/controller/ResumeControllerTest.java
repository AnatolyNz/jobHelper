package mate.academy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import mate.academy.dto.ResumeDto;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.model.User;
import mate.academy.service.ResumeParserService;
import mate.academy.service.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ResumeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResumeParserService resumeParserService;

    @Mock
    private ResumeService resumeService;

    @Mock
    private ResumeMapper resumeMapper;

    @InjectMocks
    private ResumeController resumeController;

    private ObjectMapper objectMapper;

    private ResumeDto resumeDto;
    private Resume resume;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(resumeController).build();
        objectMapper = new ObjectMapper();

        // sample data
        User user = new User();
        user.setId(1L);

        resume = new Resume();
        resume.setId(1L);
        resume.setFileName("test.pdf");
        resume.setFileType("application/pdf");
        resume.setFileData(new byte[]{1, 2, 3});
        resume.setUser(user);
        resume.setExtractedSkills(Set.of("Java", "Spring"));

        resumeDto = new ResumeDto();
        resumeDto.setId(1L);
        resumeDto.setFileName("test.pdf");
        resumeDto.setFileType("application/pdf");
        resumeDto.setUserId(1L);
        resumeDto.setExtractedSkills(List.of("Java", "Spring"));
    }

    @Test
    @DisplayName("GET /resumes/{id} - should return resume DTO")
    void getResume_ReturnsResumeDto() throws Exception {
        when(resumeService.findById(1L)).thenReturn(resume);
        when(resumeMapper.toDto(resume)).thenReturn(resumeDto);

        mockMvc.perform(get("/resumes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resumeDto.getId()))
                .andExpect(jsonPath("$.fileName").value(resumeDto.getFileName()))
                .andExpect(jsonPath("$.extractedSkills[0]").value("Java"));
    }

    @Test
    @DisplayName("POST /resumes - should create resume and return DTO")
    void createResume_ReturnsCreatedResumeDto() throws Exception {
        when(resumeService.save(any(ResumeDto.class))).thenReturn(resume);
        when(resumeMapper.toDto(resume)).thenReturn(resumeDto);

        String jsonRequest = objectMapper.writeValueAsString(resumeDto);

        mockMvc.perform(post("/resumes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resumeDto.getId()))
                .andExpect(jsonPath("$.fileName").value(resumeDto.getFileName()));
    }

    @Test
    @DisplayName("POST /resumes/upload - should upload file and return OK")
    void uploadResume_ReturnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                new byte[]{1, 2, 3}
        );

        when(resumeParserService.extractTextFromFileData(any())).thenReturn("Java, Spring");
        when(resumeParserService.extractSkills(any())).thenReturn(List.of("Java", "Spring"));
        when(resumeService.save(any(ResumeDto.class))).thenReturn(resume);

        mockMvc.perform(multipart("/resumes/upload")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Resume uploaded with ID: " + resume.getId()));
    }
}
