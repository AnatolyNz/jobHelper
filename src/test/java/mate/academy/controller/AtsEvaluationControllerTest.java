package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.AtsEvaluationDto;
import mate.academy.mapper.AtsEvaluationMapper;
import mate.academy.model.AtsEvaluation;
import mate.academy.security.JwtAuthenticationFilter;
import mate.academy.security.JwtUtil;
import mate.academy.service.AtsEvaluationService;
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

@WebMvcTest(AtsEvaluationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AtsEvaluationControllerTest {

    private static final Long RESUME_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AtsEvaluationService atsEvaluationService;

    @MockBean
    private AtsEvaluationMapper atsEvaluationMapper;

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
    @DisplayName("Evaluate resume - should return ATS evaluation DTO")
    void evaluateResume_ValidResumeId_Success() throws Exception {
        AtsEvaluation mockEntity = new AtsEvaluation();
        mockEntity.setId(RESUME_ID);
        mockEntity.setScore(85);
        mockEntity.setFeedback("Looks great for ATS!");

        when(atsEvaluationService.evaluateResume(Mockito.eq(RESUME_ID)))
                .thenReturn(mockEntity);

        when(atsEvaluationMapper.toDto(Mockito.any(AtsEvaluation.class)))
                .thenAnswer(invocation -> {
                    AtsEvaluation entity = invocation.getArgument(0);
                    AtsEvaluationDto dto = new AtsEvaluationDto();
                    dto.setResumeId(entity.getId());
                    dto.setScore(entity.getScore());
                    dto.setFeedback(entity.getFeedback());
                    return dto;
                });

        MvcResult result = mockMvc.perform(get("/ats-evaluation/resume/{id}", RESUME_ID))
                .andExpect(status().isOk())
                .andReturn();

        AtsEvaluationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                AtsEvaluationDto.class
        );

        assertEquals(RESUME_ID, actual.getResumeId());
        assertEquals(85, actual.getScore());
        assertEquals("Looks great for ATS!", actual.getFeedback());

        Mockito.verify(atsEvaluationService, Mockito.times(1))
                .evaluateResume(RESUME_ID);
    }
}
