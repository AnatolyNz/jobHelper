package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import mate.academy.dto.JobApplicationRequestDto;
import mate.academy.model.Job;
import mate.academy.model.JobApplication;
import mate.academy.model.JobApplicationStatus;
import mate.academy.model.JobApplicationStatus.Status;
import mate.academy.model.User;
import mate.academy.repository.JobApplicationRepository;
import mate.academy.repository.JobApplicationStatusRepository;
import mate.academy.repository.JobRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.impl.JobApplicationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationStatusRepository jobApplicationStatusRepository;

    @InjectMocks
    private JobApplicationServiceImpl jobApplicationService;

    @Test
    @DisplayName("Should return all job applications for a user")
    void findByUserId_ShouldReturnApplications() {
        User user = new User();
        user.setId(1L);

        JobApplication app1 = new JobApplication();
        app1.setUser(user);

        JobApplication app2 = new JobApplication();
        app2.setUser(user);

        when(jobApplicationRepository.findByUserIdWithDetails(1L))
                .thenReturn(List.of(app1, app2));

        List<JobApplication> result = jobApplicationService.findByUserId(1L);

        assertEquals(2, result.size(),
                "Expected 2 job applications for the user");
        assertEquals(user, result.get(0).getUser(),
                "First application should belong to the correct user");
        assertEquals(user, result.get(1).getUser(),
                "Second application should belong to the correct user");
        verify(jobApplicationRepository, times(1))
                .findByUserIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should return empty list when user has no applications")
    void findByUserId_NoApplications_ShouldReturnEmptyList() {
        when(jobApplicationRepository.findByUserIdWithDetails(1L))
                .thenReturn(List.of());

        List<JobApplication> result = jobApplicationService.findByUserId(1L);

        assertTrue(result.isEmpty(), "Expected empty list when user has no applications");
        verify(jobApplicationRepository, times(1)).findByUserIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should return job application by ID if found")
    void findById_ValidId_ShouldReturnApplication() {
        JobApplication app = new JobApplication();
        app.setStatus(new JobApplicationStatus());

        when(jobApplicationRepository.findByIdWithDetails(1L)).thenReturn(app);

        JobApplication result = jobApplicationService.findById(1L);

        assertNotNull(result, "Expected job application to be returned");
        assertNotNull(result.getStatus(), "Expected job application to have a status");
        verify(jobApplicationRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should throw exception when job application not found by ID")
    void findById_NotFound_ShouldThrowException() {
        when(jobApplicationRepository.findByIdWithDetails(999L)).thenReturn(null);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> jobApplicationService.findById(999L),
                "Expected NoSuchElementException when application is not found"
        );

        assertTrue(exception.getMessage().contains("Job application not found"));
        verify(jobApplicationRepository, times(1)).findByIdWithDetails(999L);
    }

    @Test
    @DisplayName("Should create a new job application with PENDING status")
    void create_ShouldReturnCreatedApplication() {
        JobApplicationRequestDto requestDto = new JobApplicationRequestDto();
        requestDto.setUserId(1L);
        requestDto.setJobId(10L);

        User user = new User();
        user.setId(1L);

        Job job = new Job();
        job.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jobRepository.findByIdWithRequiredSkills(10L)).thenReturn(Optional.of(job));
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jobApplicationStatusRepository.save(any(JobApplicationStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication result = jobApplicationService.create(requestDto);

        assertNotNull(result, "Expected created job application to be returned");
        assertEquals(user, result.getUser(), "Expected application user to match request");
        assertEquals(job, result.getJob(), "Expected application job to match request");
        assertEquals(Status.PENDING, result.getStatus().getStatus(),
                "Expected application status to be PENDING");

        verify(jobApplicationRepository, times(1))
                .save(any(JobApplication.class));
        verify(jobApplicationStatusRepository, times(1))
                .save(any(JobApplicationStatus.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found on create")
    void create_UserNotFound_ShouldThrowException() {
        JobApplicationRequestDto requestDto = new JobApplicationRequestDto();
        requestDto.setUserId(1L);
        requestDto.setJobId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> jobApplicationService.create(requestDto),
                "Expected exception when user not found"
        );

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should throw exception when job not found on create")
    void create_JobNotFound_ShouldThrowException() {
        JobApplicationRequestDto requestDto = new JobApplicationRequestDto();
        requestDto.setUserId(1L);
        requestDto.setJobId(10L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jobRepository.findByIdWithRequiredSkills(10L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> jobApplicationService.create(requestDto),
                "Expected exception when job not found"
        );

        assertTrue(exception.getMessage().contains("Job not found"));
    }

    @Test
    @DisplayName("Should update status of existing job application")
    void updateStatus_ShouldUpdateStatus() {
        JobApplicationStatus status = new JobApplicationStatus();
        status.setStatus(Status.PENDING);

        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setStatus(status);

        when(jobApplicationRepository.findByIdWithDetails(1L)).thenReturn(app);
        when(jobApplicationStatusRepository.save(any(JobApplicationStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        JobApplication result = jobApplicationService.updateStatus(1L, Status.HIRED);

        assertEquals(Status.HIRED, result.getStatus().getStatus(),
                "Expected status to be updated to HIRED");
        verify(jobApplicationStatusRepository, times(1)).save(status);
    }

    @Test
    @DisplayName("Should throw exception when updating status of non-existent application")
    void updateStatus_NotFound_ShouldThrowException() {
        when(jobApplicationRepository.findByIdWithDetails(99L)).thenReturn(null);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> jobApplicationService.updateStatus(99L, Status.REJECTED),
                "Expected exception when application does not exist"
        );

        assertTrue(exception.getMessage().contains("Job application not found"));
    }

    @Test
    @DisplayName("Should delete existing job application")
    void delete_ExistingApplication_ShouldDelete() {
        when(jobApplicationRepository.existsById(1L)).thenReturn(true);

        jobApplicationService.delete(1L);

        verify(jobApplicationRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent application")
    void delete_NotFound_ShouldThrowException() {
        when(jobApplicationRepository.existsById(99L)).thenReturn(false);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> jobApplicationService.delete(99L),
                "Expected exception when application does not exist"
        );

        assertTrue(exception.getMessage().contains("Job application not found"));
    }
}
