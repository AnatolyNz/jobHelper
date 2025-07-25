package mate.academy.service.impl;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.ResumeDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.ResumeMapper;
import mate.academy.model.Resume;
import mate.academy.model.Skill;
import mate.academy.repository.ResumeRepository;
import mate.academy.repository.SkillRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    private final ResumeMapper resumeMapper;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    public Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + id));
    }

    @Override
    public Resume save(MultipartFile file, Long userId) {
        Resume resume = new Resume();
        resume.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId)));

        try {
            resume.setFileName(file.getOriginalFilename());
            resume.setFileType(file.getContentType());
            resume.setFilePath("N/A");
            resume.setFileData(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store resume file", e);
        }

        return resumeRepository.save(resume);
    }

    @Override
    public Resume save(ResumeDto resumeDto) {
        Resume resume = resumeMapper.toEntity(resumeDto);

        resume.setUser(userRepository.findById(resumeDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: "
                        + resumeDto.getUserId())));

        if (resumeDto.getExtractedSkills() != null) {
            List<Skill> skills = resumeDto.getExtractedSkills().stream()
                    .map(name -> name.trim().toLowerCase())
                    .distinct()
                    .map(name -> skillRepository.findByNameIgnoreCase(name)
                            .orElseGet(() -> skillRepository.save(new Skill(name))))
                    .toList();
            resume.setSkills(skills);
        }

        return resumeRepository.save(resume);
    }
}
