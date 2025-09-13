package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.ResumeDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.model.Skill;
import mate.academy.model.User;
import mate.academy.repository.ResumeRepository;
import mate.academy.repository.SkillRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.impl.ResumeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ResumeServiceTest {
    @Mock private ResumeMapper resumeMapper;
    @Mock private ResumeRepository resumeRepository;
    @Mock private SkillRepository skillRepository;
    @Mock private UserRepository userRepository;
    @Mock private MultipartFile multipartFile;

    @InjectMocks private ResumeServiceImpl resumeService;

    @Test
    @DisplayName("Should return resume with details when found")
    void findById_ValidId_ShouldReturnResume() {
        Resume resume = new Resume();
        when(resumeRepository.findByIdWithAllDetails(1L)).thenReturn(Optional.of(resume));

        Resume result = resumeService.findById(1L);

        assertEquals(resume, result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when resume not found")
    void findById_NotFound_ShouldThrowException() {
        when(resumeRepository.findByIdWithAllDetails(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> resumeService.findById(1L));
    }

    @Test
    @DisplayName("Should save resume from file for existing user")
    void saveFile_ShouldReturnSavedResume() throws IOException {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(multipartFile.getOriginalFilename()).thenReturn("resume.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2, 3});

        Resume saved = new Resume();
        when(resumeRepository.save(any(Resume.class))).thenReturn(saved);

        Resume result = resumeService.save(multipartFile, 1L);

        assertEquals(saved, result);
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user not found for file upload")
    void saveFile_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> resumeService.save(multipartFile, 1L));
    }

    @Test
    @DisplayName("Should save resume from ResumeDto with extracted skills")
    void saveResumeDto_ShouldReturnSavedResume() {
        ResumeDto resumeDto = new ResumeDto();
        resumeDto.setUserId(1L);
        resumeDto.setExtractedSkills(List.of("Java", "Spring"));
        Resume resume = new Resume();

        when(resumeMapper.toEntity(resumeDto)).thenReturn(resume);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(skillRepository.findByNameIgnoreCase("java")).thenReturn(Optional.empty());
        when(skillRepository.findByNameIgnoreCase("spring")).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation ->
                invocation.getArgument(0));
        when(resumeRepository.save(resume)).thenReturn(resume);

        Resume result = resumeService.save(resumeDto);

        assertEquals(resume, result);
        assertFalse(resume.getSkills().isEmpty());
    }
}
