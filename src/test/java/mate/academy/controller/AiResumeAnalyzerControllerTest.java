package mate.academy.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.AiAnalyzedResumeDto;
import mate.academy.security.JwtAuthenticationFilter;
import mate.academy.security.JwtUtil;
import mate.academy.service.AiResumeAnalyzerService;
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

@WebMvcTest(AiResumeAnalyzerController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiResumeAnalyzerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiResumeAnalyzerService analyzerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private EmailServiceImpl emailServiceImpl;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Analyze resume - should return 200 and AiAnalyzedResumeDto")
    void analyzeResume_ReturnsOkDto() throws Exception {
        AiAnalyzedResumeDto dto = new AiAnalyzedResumeDto();
        dto.setId(100L);
        dto.setResumeFileName("java_backend_resume.pdf");
        dto.setAtsScore("85%");
        dto.setGrammarFeedback("No major grammar errors found.");
        dto.setStructureFeedback("Clear and well-structured.");
        dto.setActionVerbSuggestions("Consider adding more strong verbs.");
        dto.setQuantificationSuggestions("Quantify achievements where possible.");
        dto.setSoftSkillsInferred("Teamwork, communication.");
        dto.setToneFeedback("Positive and professional.");

        Long resumeId = 1L;
        Mockito.when(analyzerService.analyzeResume(eq(resumeId))).thenReturn(dto);

        mockMvc.perform(get("/ai-resume-analysis/{resumeId}", resumeId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        Mockito.verify(analyzerService, Mockito.times(1))
                .analyzeResume(resumeId);
    }
}
