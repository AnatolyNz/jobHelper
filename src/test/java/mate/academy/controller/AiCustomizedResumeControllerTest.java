package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import mate.academy.dto.AiCustomizedResumeDto;
import mate.academy.model.AiCustomizedResume;
import mate.academy.model.Job;
import mate.academy.model.Resume;
import mate.academy.security.JwtAuthenticationFilter;
import mate.academy.security.JwtUtil;
import mate.academy.service.AiCustomizedResumeService;
import mate.academy.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(AiCustomizedResumeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiCustomizedResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiCustomizedResumeService aiCustomizedResumeService;

    @MockBean
    private EmailServiceImpl emailServiceImpl;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Generate AI-customized resume - should return 201 and AiCustomizedResumeDto")
    void generateCustomizedResume_ReturnsCreatedDto() throws Exception {
        Long resumeId = 1L;
        Long jobId = 2L;

        Resume resume = new Resume();
        resume.setId(resumeId);
        Job job = new Job();
        job.setId(jobId);

        AiCustomizedResume aiCustomizedResume = new AiCustomizedResume();
        aiCustomizedResume.setId(100L);
        aiCustomizedResume.setResume(resume);
        aiCustomizedResume.setJob(job);
        aiCustomizedResume.setContent("Tailored resume content");
        aiCustomizedResume.setGeneratedAt(LocalDateTime.now());

        Mockito.when(aiCustomizedResumeService.generateCustomizedResume(eq(resumeId), eq(jobId)))
                .thenReturn(aiCustomizedResume);

        MvcResult result = mockMvc.perform(
                        post("/ai-custom-resume/{resumeId}/job/{jobId}", resumeId, jobId)
                                .with(csrf())
                                .contentType("application/json")
                )
                .andExpect(status().isCreated())
                .andReturn();

        AiCustomizedResumeDto response = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                AiCustomizedResumeDto.class
        );

        assertEquals(aiCustomizedResume.getId(), response.id());
        assertEquals(resumeId, response.resumeId());
        assertEquals(jobId, response.jobId());
        assertEquals(aiCustomizedResume.getContent(), response.content());

        Mockito.verify(aiCustomizedResumeService, Mockito.times(1))
                .generateCustomizedResume(resumeId, jobId);
    }
}
