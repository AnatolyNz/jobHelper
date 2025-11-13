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
import java.util.List;
import mate.academy.dto.JobApplicationDto;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.dto.JobApplicationUpdateDto;
import mate.academy.mapper.JobApplicationMapper;
import mate.academy.model.Job;
import mate.academy.model.JobApplication;
import mate.academy.model.JobApplicationStatus;
import mate.academy.model.JobApplicationStatus.Status;
import mate.academy.model.Skill;
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

    private Job job;
    private User user;
    private JobApplicationStatus status;
    private JobApplication jobApplication;
    private JobApplicationDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(jobApplicationController).build();
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(1L);

        job = new Job();
        job.setId(100L);
        job.setTitle("Backend Developer");
        job.setCompany("Mate Academy");
        job.setLocation("Kyiv");
        job.setWorkFormat(Job.WorkFormat.Віддалений);
        job.setRequiredSkills(List.of(new Skill("Java"), new Skill("Spring")));

        status = new JobApplicationStatus();
        status.setStatus(Status.PENDING);

        jobApplication = new JobApplication();
        jobApplication.setId(1L);
        jobApplication.setUser(user);
        jobApplication.setJob(job);
        jobApplication.setStatus(status);

        dto = new JobApplicationDto();
        dto.setId(1L);
        dto.setUserId(user.getId());
        dto.setJobId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setCompany(job.getCompany());
        dto.setStatus("PENDING");
        dto.setRequiredSkills(List.of("Java", "Spring"));
        dto.setWorkFormat("Віддалений");
    }

    @Test
    void getUserApplications_ShouldReturnListWithSkillsAndWorkFormat() throws Exception {
        when(jobApplicationService.findByUserId(1L)).thenReturn(List.of(jobApplication));
        when(jobApplicationMapper.toDto(jobApplication)).thenReturn(dto);

        mockMvc.perform(get("/applications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].jobId").value(100L))
                .andExpect(jsonPath("$[0].title").value("Backend Developer"))
                .andExpect(jsonPath("$[0].company").value("Mate Academy"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].requiredSkills[0]").value("Java"))
                .andExpect(jsonPath("$[0].requiredSkills[1]").value("Spring"))
                .andExpect(jsonPath("$[0].workFormat").value("Віддалений"));
    }

    @Test
    void getById_ShouldReturnApplicationWithSkillsAndWorkFormat() throws Exception {
        when(jobApplicationService.findById(1L)).thenReturn(jobApplication);
        when(jobApplicationMapper.toDto(jobApplication)).thenReturn(dto);

        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.jobId").value(100L))
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.company").value("Mate Academy"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.requiredSkills[0]").value("Java"))
                .andExpect(jsonPath("$.requiredSkills[1]").value("Spring"))
                .andExpect(jsonPath("$.workFormat").value("Віддалений"));
    }

    @Test
    void create_ShouldReturnCreatedApplication() throws Exception {
        JobApplicationRequestDto requestDto = new JobApplicationRequestDto();
        requestDto.setUserId(user.getId());
        requestDto.setJobId(job.getId());

        when(jobApplicationService.create(requestDto)).thenReturn(jobApplication);
        when(jobApplicationMapper.toDto(jobApplication)).thenReturn(dto);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.jobId").value(100L))
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.company").value("Mate Academy"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.requiredSkills[0]").value("Java"))
                .andExpect(jsonPath("$.requiredSkills[1]").value("Spring"))
                .andExpect(jsonPath("$.workFormat").value("Віддалений"));
    }

    @Test
    void updateStatus_ShouldReturnUpdatedApplication() throws Exception {
        JobApplicationUpdateDto updateDto = new JobApplicationUpdateDto();
        updateDto.setStatus(Status.HIRED);

        JobApplicationStatus newStatus = new JobApplicationStatus();
        newStatus.setStatus(Status.HIRED);

        JobApplication updatedApplication = new JobApplication();
        updatedApplication.setId(1L);
        updatedApplication.setUser(user);
        updatedApplication.setJob(job);
        updatedApplication.setStatus(newStatus);

        JobApplicationDto updatedDto = new JobApplicationDto();
        updatedDto.setId(1L);
        updatedDto.setUserId(user.getId());
        updatedDto.setJobId(job.getId());
        updatedDto.setTitle(job.getTitle());
        updatedDto.setCompany(job.getCompany());
        updatedDto.setStatus("HIRED");
        updatedDto.setRequiredSkills(List.of("Java", "Spring"));
        updatedDto.setWorkFormat("Віддалений");

        when(jobApplicationService.updateStatus(1L, updateDto.getStatus()))
                .thenReturn(updatedApplication);
        when(jobApplicationMapper.toDto(updatedApplication)).thenReturn(updatedDto);

        mockMvc.perform(put("/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("HIRED"))
                .andExpect(jsonPath("$.requiredSkills[0]").value("Java"))
                .andExpect(jsonPath("$.requiredSkills[1]").value("Spring"))
                .andExpect(jsonPath("$.workFormat").value("Віддалений"));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(jobApplicationService).delete(1L);

        mockMvc.perform(delete("/applications/1"))
                .andExpect(status().isNoContent());
    }
}
