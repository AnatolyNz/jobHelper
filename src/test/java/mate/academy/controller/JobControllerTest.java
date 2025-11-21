package mate.academy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.dto.JobDto;
import mate.academy.mapper.JobMapper;
import mate.academy.model.Job;
import mate.academy.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class JobControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobService jobService;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobController jobController;

    private ObjectMapper objectMapper;
    private Job job;
    private JobDto jobDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        objectMapper = new ObjectMapper();

        job = new Job();
        job.setId(1L);
        job.setTitle("Backend Developer");
        job.setDescription("Responsible for backend services");
        job.setCompany("TechCorp");
        job.setLocation("Remote");

        jobDto = new JobDto();
        jobDto.setId(1L);
        jobDto.setTitle("Backend Developer");
        jobDto.setDescription("Responsible for backend services");
        jobDto.setCompany("TechCorp");
        jobDto.setLocation("Remote");
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    @DisplayName("GET /jobs - should return list of jobs")
    void getAllJobs_ReturnsList() throws Exception {
        when(jobService.findAll()).thenReturn(List.of(job));
        when(jobMapper.toDto(job)).thenReturn(jobDto);

        mockMvc.perform(get("/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Backend Developer"));
    }

    @Test
    @DisplayName("GET /jobs/{id} - should return job by ID")
    void getJob_ReturnsJobDto() throws Exception {
        when(jobService.findById(1L)).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobDto);

        mockMvc.perform(get("/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Backend Developer"));
    }

    @Test
    @DisplayName("POST /jobs - should create job and return DTO")
    void createJob_ReturnsCreatedJobDto() throws Exception {
        when(jobService.save(any(JobDto.class))).thenReturn(job);
        when(jobMapper.toDto(job)).thenReturn(jobDto);

        mockMvc.perform(post("/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(jobDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Backend Developer"));
    }

    @Test
    @DisplayName("PUT /jobs/{id} - should update job and return DTO")
    void updateJob_ReturnsUpdatedJobDto() throws Exception {
        Job updatedJob = new Job();
        updatedJob.setId(1L);
        updatedJob.setTitle("Senior Backend Developer");
        updatedJob.setDescription("Updated desc");
        updatedJob.setCompany("TechCorp");
        updatedJob.setLocation("Remote");

        JobDto updatedDto = new JobDto();
        updatedDto.setId(1L);
        updatedDto.setTitle("Senior Backend Developer");
        updatedDto.setDescription("Updated desc");
        updatedDto.setCompany("TechCorp");
        updatedDto.setLocation("Remote");

        when(jobService.update(1L, updatedDto)).thenReturn(updatedJob);
        when(jobMapper.toDto(updatedJob)).thenReturn(updatedDto);

        mockMvc.perform(put("/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Backend Developer"))
                .andExpect(jsonPath("$.description").value("Updated desc"));
    }

    @Test
    @DisplayName("DELETE /jobs/{id} - should delete job and return 204")
    void deleteJob_ReturnsNoContent() throws Exception {
        doNothing().when(jobService).delete(1L);

        mockMvc.perform(delete("/jobs/1"))
                .andExpect(status().isNoContent());

        verify(jobService, times(1)).delete(1L);
    }
}
