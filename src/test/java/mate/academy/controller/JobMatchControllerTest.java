package mate.academy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.dto.JobMatchDto;
import mate.academy.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.mail.host=localhost",
                "spring.mail.port=25",
                "spring.mail.username=test@example.com",
                "spring.mail.password=test"
        }
)
@AutoConfigureMockMvc
@Sql(scripts = "/database/job_match_test_data.sql")
class JobMatchControllerTest {

    private static final Long RESUME_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mock EmailService so Spring doesn't try to load EmailServiceImpl
     * and fail due to missing @Value properties.
     */
    @MockBean
    private EmailService emailService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @DisplayName("Find matching jobs for resume - should return job matches")
    void getMatchingJobs_ReturnsList() throws Exception {
        // Perform GET request as a mock authenticated user
        MvcResult result = mockMvc.perform(get("/job-matches/resume/{id}", RESUME_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<JobMatchDto> matches = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );

        assertFalse(matches.isEmpty(), "Expected non-empty list of job matches");
        assertEquals(RESUME_ID, matches.get(0).getResumeId(), "Resume ID should match");
    }
}
