package mate.academy.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import mate.academy.dto.JobApplicationDto;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.dto.JobApplicationUpdateDto;
import mate.academy.mapper.JobApplicationMapper;
import mate.academy.model.Job;
import mate.academy.model.JobApplication;
import mate.academy.model.JobApplicationStatus;
import mate.academy.model.JobApplicationStatus.Status;
import mate.academy.model.User;
import mate.academy.service.JobApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class JobApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobApplicationService jobApplicationService;

    @Mock
    private JobApplicationMapper jobApplicationMapper;

    @InjectMocks
    private JobApplicationController jobApplicationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobApplicationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUserApplications_ShouldReturnList() throws Exception {
        User user = new User();
        user.setId(1L);

        Job job = new Job();
        job.setId(100L);
        job.setTitle("Backend Developer");
        job.setCompany("Mate Academy");

        JobApplicationStatus status = new JobApplicationStatus();
        status.setStatus(Status.PENDING);

        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(1L);
        jobApplication.setUser(user);
        jobApplication.setJob(job);
        jobApplication.setStatus(status);

        JobApplicationDto jobApplicationDto = new JobApplicationDto();
        jobApplicationDto.setId(1L);
        jobApplicationDto.setUserId(1L);
        jobApplicationDto.setJobId(100L);
        jobApplicationDto.setJobTitle("Backend Developer");
        jobApplicationDto.setCompany("Mate Academy");
        jobApplicationDto.setStatus("PENDING");
        jobApplicationDto.setAppliedAt(LocalDateTime.of(2025, 9, 19, 12, 0));

        when(jobApplicationService.findByUserId(1L)).thenReturn(List.of(jobApplication));
        when(jobApplicationMapper.toDto(jobApplication)).thenReturn(jobApplicationDto);

        mockMvc.perform(get("/applications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].jobId").value(100L))
                .andExpect(jsonPath("$[0].jobTitle").value("Backend Developer"))
                .andExpect(jsonPath("$[0].company").value("Mate Academy"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].appliedAt").value("2025-09-19T12:00:00"));
    }

    @Test
    void create_ShouldReturnCreatedApplication() throws Exception {
        JobApplicationRequestDto requestDto = new JobApplicationRequestDto();
        requestDto.setUserId(1L);
        requestDto.setJobId(100L);

        User user = new User();
        user.setId(1L);

        Job job = new Job();
        job.setId(100L);
        job.setTitle("Backend Developer");
        job.setCompany("Mate Academy");

        JobApplicationStatus status = new JobApplicationStatus();
        status.setStatus(Status.PENDING);

        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(1L);
        jobApplication.setUser(user);
        jobApplication.setJob(job);
        jobApplication.setStatus(status);

        JobApplicationDto jobApplicationDto = new JobApplicationDto();
        jobApplicationDto.setId(1L);
        jobApplicationDto.setUserId(1L);
        jobApplicationDto.setJobId(100L);
        jobApplicationDto.setJobTitle("Backend Developer");
        jobApplicationDto.setCompany("Mate Academy");
        jobApplicationDto.setStatus("PENDING");
        jobApplicationDto.setAppliedAt(LocalDateTime.of(2025, 9, 19, 12, 0));

        when(jobApplicationService.create(requestDto)).thenReturn(jobApplication);
        when(jobApplicationMapper.toDto(jobApplication)).thenReturn(jobApplicationDto);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.jobId").value(100L))
                .andExpect(jsonPath("$.jobTitle").value("Backend Developer"))
                .andExpect(jsonPath("$.company").value("Mate Academy"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.appliedAt").value("2025-09-19T12:00:00"));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedApplication() throws Exception {
        JobApplicationUpdateDto updateDto = new JobApplicationUpdateDto();
        updateDto.setStatus(Status.HIRED); // use existing enum value

        User user = new User();
        user.setId(1L);

        Job job = new Job();
        job.setId(100L);
        job.setTitle("Backend Developer");
        job.setCompany("Mate Academy");

        JobApplicationStatus status = new JobApplicationStatus();
        status.setStatus(Status.HIRED); // existing enum

        JobApplication updatedApplication = new JobApplication();
        updatedApplication.setId(1L);
        updatedApplication.setUser(user);
        updatedApplication.setJob(job);
        updatedApplication.setStatus(status);

        JobApplicationDto updatedDto = new JobApplicationDto();
        updatedDto.setId(1L);
        updatedDto.setUserId(1L);
        updatedDto.setJobId(100L);
        updatedDto.setJobTitle("Backend Developer");
        updatedDto.setCompany("Mate Academy");
        updatedDto.setStatus("HIRED");
        updatedDto.setAppliedAt(LocalDateTime.of(2025, 9, 19, 12, 0));

        when(jobApplicationService.updateStatus(1L, updateDto.getStatus()))
                .thenReturn(updatedApplication);
        when(jobApplicationMapper.toDto(updatedApplication)).thenReturn(updatedDto);

        mockMvc.perform(put("/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HIRED"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(jobApplicationService).delete(1L);

        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNoContent());
    }
}
