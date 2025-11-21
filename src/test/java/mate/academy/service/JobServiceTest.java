package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.JobDto;
import mate.academy.mapper.JobMapper;
import mate.academy.model.Job;
import mate.academy.model.Skill;
import mate.academy.repository.JobRepository;
import mate.academy.repository.SkillRepository;
import mate.academy.service.impl.JobServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock private JobMapper jobMapper;
    @Mock private JobRepository jobRepository;
    @Mock private SkillRepository skillRepository;

    @InjectMocks private JobServiceImpl jobService;

    @Test
    @DisplayName("Should save job with required skills, creating missing skills")
    void saveJob_ShouldReturnSavedJob() {
        JobDto jobDto = new JobDto();
        jobDto.setTitle("Backend Developer");
        jobDto.setRequiredSkills(List.of("Java", "Spring"));

        Job job = new Job();
        job.setJobApplications(new ArrayList<>());

        Skill javaSkill = new Skill("Java");
        Skill springSkill = new Skill("Spring");

        when(jobMapper.toModel(jobDto)).thenReturn(job);
        when(skillRepository.findByNameIgnoreCase("Java"))
                .thenReturn(Optional.of(javaSkill));
        when(skillRepository.findByNameIgnoreCase("Spring"))
                .thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jobRepository.save(job)).thenReturn(job);

        Job result = jobService.save(jobDto);

        assertEquals(job, result);
        assertEquals(2, result.getRequiredSkills().size());
        verify(jobRepository).save(job);
    }

    @Test
    @DisplayName("Should return job by ID if found")
    void findById_ValidId_ShouldReturnJob() {
        Job job = new Job();
        job.setId(1L);
        job.setRequiredSkills(List.of(new Skill("Java")));
        job.setJobApplications(new ArrayList<>());
        job.setJobMatches(new ArrayList<>());

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Job result = jobService.findById(1L);

        assertEquals(job, result);
        assertNotNull(result.getJobApplications());
        assertNotNull(result.getJobMatches());
        assertTrue(result.getJobApplications().isEmpty());
        assertTrue(result.getJobMatches().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when job not found by ID")
    void findById_NotFound_ShouldThrowException() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> jobService.findById(1L));
    }

    @Test
    @DisplayName("Should return list of all jobs")
    void findAll_ShouldReturnJobList() {
        Job job1 = new Job();
        job1.setId(1L);
        job1.setRequiredSkills(List.of(new Skill("Java")));
        job1.setJobApplications(new ArrayList<>());
        job1.setJobMatches(new ArrayList<>());

        Job job2 = new Job();
        job2.setId(2L);
        job2.setRequiredSkills(List.of(new Skill("Spring")));
        job2.setJobApplications(new ArrayList<>());
        job2.setJobMatches(new ArrayList<>());

        when(jobRepository.findAll()).thenReturn(List.of(job1, job2));

        List<Job> jobs = jobService.findAll();

        assertEquals(2, jobs.size());
        assertTrue(jobs.stream().allMatch(j -> j.getJobApplications() != null));
        assertTrue(jobs.stream().allMatch(j -> j.getJobMatches() != null));
    }

    @Test
    @DisplayName("Should update existing job with valid data")
    void updateJob_ShouldReturnUpdatedJob() {
        JobDto jobDto = new JobDto();
        jobDto.setTitle("Updated Title");
        jobDto.setDescription("Updated Desc");
        jobDto.setCompany("Updated Company");
        jobDto.setLocation("Updated Location");
        jobDto.setSalary(BigDecimal.valueOf(1000.0));
        jobDto.setWorkFormat("Віддалений");
        jobDto.setRequiredSkills(List.of("Java"));

        Job existingJob = new Job();
        existingJob.setRequiredSkills(new ArrayList<>());

        when(jobRepository.findById(1L)).thenReturn(Optional.of(existingJob));
        when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jobRepository.save(existingJob)).thenReturn(existingJob);

        Job result = jobService.update(1L, jobDto);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals("Updated Company", result.getCompany());
        assertEquals("Updated Location", result.getLocation());
        assertEquals(BigDecimal.valueOf(1000.0), result.getSalary());
        assertEquals(Job.WorkFormat.Віддалений, result.getWorkFormat());
        assertEquals(1, result.getRequiredSkills().size());
    }

    @Test
    @DisplayName("Should throw exception when updating job with invalid work format")
    void updateJob_InvalidWorkFormat_ShouldThrowException() {
        JobDto jobDto = new JobDto();
        jobDto.setWorkFormat("INVALID");

        Job existingJob = new Job();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existingJob));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jobService.update(1L, jobDto));

        assertTrue(exception.getMessage().contains("Invalid work format"));
    }

    @Test
    @DisplayName("Should throw exception when job to update not found")
    void updateJob_NotFound_ShouldThrowException() {
        JobDto jobDto = new JobDto();
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> jobService.update(1L, jobDto));
    }

    @Test
    @DisplayName("Should delete job by ID")
    void deleteJob_ShouldCallRepository() {
        doNothing().when(jobRepository).deleteById(1L);
        jobService.delete(1L);
        verify(jobRepository).deleteById(1L);
    }
}
