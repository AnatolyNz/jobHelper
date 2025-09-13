package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private JobMapper jobMapper;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private JobServiceImpl jobService;

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

        when(skillRepository.findByNameIgnoreCase("Java")).thenReturn(Optional.of(javaSkill));
        when(skillRepository.findByNameIgnoreCase("Spring")).thenReturn(Optional.empty());

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

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        Job result = jobService.findById(1L);

        assertEquals(job, result);
        assertNotNull(result.getJobApplications());
        assertTrue(result.getJobApplications().isEmpty());
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

        Job job2 = new Job();
        job2.setId(2L);
        job2.setRequiredSkills(List.of(new Skill("Spring")));
        job2.setJobApplications(new ArrayList<>());

        when(jobRepository.findAll()).thenReturn(List.of(job1, job2));

        List<Job> jobs = jobService.findAll();

        assertEquals(2, jobs.size());
        assertTrue(jobs.stream().allMatch(j -> j.getJobApplications() != null));
    }
}
