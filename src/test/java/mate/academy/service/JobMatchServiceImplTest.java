package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.dto.JobMatchDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Job;
import mate.academy.model.Resume;
import mate.academy.model.Skill;
import mate.academy.repository.JobRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.impl.JobMatchServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class JobMatchServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobMatchServiceImpl jobMatchService;

    private Skill skill(String name) {
        Skill s = new Skill();
        s.setName(name);
        return s;
    }

    @Test
    @DisplayName("Should return job match for specific job when jobId is provided")
    void findMatchesForResume_SingleJob_ShouldReturnSingleMatch() {
        Skill java = skill("Java");
        Skill spring = skill("Spring");

        Resume resume = new Resume();
        resume.setId(2L);
        resume.setSkills(Set.of(java, spring));

        Job job = new Job();
        job.setId(99L);
        job.setTitle("Spring Developer");
        job.setRequiredSkills(List.of(java, spring));

        when(resumeRepository.findByIdWithSkills(2L)).thenReturn(Optional.of(resume));
        when(jobRepository.findByIdWithRequiredSkills(99L)).thenReturn(Optional.of(job));

        List<JobMatchDto> result = jobMatchService.findMatchesForResume(2L, 99L);

        assertEquals(1, result.size());
        JobMatchDto dto = result.get(0);
        assertEquals(99L, dto.getJobId());
        assertEquals(2L, dto.getResumeId());
        assertEquals(100.0, dto.getMatchScore());
        assertEquals("Spring Developer", dto.getJobTitle());

        verify(resumeRepository, times(1)).findByIdWithSkills(2L);
        verify(jobRepository, times(1)).findByIdWithRequiredSkills(99L);
    }

    @Test
    @DisplayName("Should return job matches for all jobs with pagination")
    void findMatchesForResume_AllJobs_ShouldReturnAllMatches() {
        Skill java = skill("Java");
        Skill spring = skill("Spring");

        Resume resume = new Resume();
        resume.setId(1L);
        resume.setSkills(Set.of(java, spring));

        Job job1 = new Job();
        job1.setId(10L);
        job1.setTitle("Backend Developer");
        job1.setRequiredSkills(List.of(java, spring));

        Job job2 = new Job();
        job2.setId(20L);
        job2.setTitle("Java Engineer");
        job2.setRequiredSkills(List.of(java));

        Job job3 = new Job();
        job3.setId(30L);
        job3.setTitle("Spring Boot Dev");
        job3.setRequiredSkills(List.of(spring));

        when(resumeRepository.findByIdWithSkills(1L)).thenReturn(Optional.of(resume));
        when(jobRepository.findAllWithRequiredSkills(PageRequest.of(0, 50)))
                .thenReturn(new PageImpl<>(List.of(job1, job2)));
        when(jobRepository.findAllWithRequiredSkills(PageRequest.of(1, 50)))
                .thenReturn(new PageImpl<>(List.of(job3)));
        when(jobRepository.findAllWithRequiredSkills(PageRequest.of(2, 50)))
                .thenReturn(new PageImpl<>(List.of()));

        List<JobMatchDto> result = jobMatchService.findMatchesForResume(1L, null);

        assertEquals(3, result.size());
        verify(resumeRepository, times(1)).findByIdWithSkills(1L);
        verify(jobRepository, times(3)).findAllWithRequiredSkills(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when resume not found")
    void findMatchesForResume_ResumeNotFound_ShouldThrow() {
        when(resumeRepository.findByIdWithSkills(123L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> jobMatchService.findMatchesForResume(123L, null));

        verify(resumeRepository, times(1)).findByIdWithSkills(123L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when job not found by ID")
    void findMatchesForResume_JobNotFound_ShouldThrow() {
        Resume resume = new Resume();
        resume.setId(5L);
        resume.setSkills(new HashSet<>()); // initialize skills!

        when(resumeRepository.findByIdWithSkills(5L)).thenReturn(Optional.of(resume));
        when(jobRepository.findByIdWithRequiredSkills(777L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> jobMatchService.findMatchesForResume(5L, 777L));

        verify(resumeRepository, times(1)).findByIdWithSkills(5L);
        verify(jobRepository, times(1)).findByIdWithRequiredSkills(777L);
    }

    @Test
    @DisplayName("Should correctly calculate partial match score (e.g., 2 of 3 = 66.67%)")
    void findMatchesForResume_PartialMatch_ShouldCalculateScore() {
        Skill java = skill("Java");
        Skill docker = skill("Docker");

        Resume resume = new Resume();
        resume.setId(10L);
        resume.setSkills(Set.of(java, docker));

        Job job = new Job();
        job.setId(100L);
        job.setTitle("Cloud Engineer");
        Skill spring = skill("Spring");
        job.setRequiredSkills(List.of(java, spring, docker)); // 2 of 3 match

        when(resumeRepository.findByIdWithSkills(10L)).thenReturn(Optional.of(resume));
        when(jobRepository.findAllWithRequiredSkills(PageRequest.of(0, 50)))
                .thenReturn(new PageImpl<>(List.of(job)));
        when(jobRepository.findAllWithRequiredSkills(PageRequest.of(1, 50)))
                .thenReturn(new PageImpl<>(List.of()));

        List<JobMatchDto> result = jobMatchService.findMatchesForResume(10L, null);

        assertEquals(1, result.size());
        JobMatchDto dto = result.get(0);
        assertEquals(66.666, dto.getMatchScore(), 0.1);
        assertEquals("Cloud Engineer", dto.getJobTitle());

        verify(resumeRepository, times(1)).findByIdWithSkills(10L);
        verify(jobRepository, times(2)).findAllWithRequiredSkills(any(PageRequest.class));
    }
}
