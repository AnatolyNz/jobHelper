package mate.academy.service.impl;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    private final ResumeMapper resumeMapper;
    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Resume findById(Long id) {
        return resumeRepository.findByIdWithAllDetails(id)
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

        resume.setFilePath("N/A");

        if (resumeDto.getExtractedSkills() != null && !resumeDto.getExtractedSkills().isEmpty()) {
            Set<String> normalizedNames = resumeDto.getExtractedSkills().stream()
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .map(name -> name.trim().toLowerCase())
                    .collect(Collectors.toSet());

            Set<Skill> skills = normalizedNames.stream()
                    .map(name -> skillRepository.findByNameIgnoreCase(name)
                            .orElseGet(() -> skillRepository.save(new Skill(name))))
                    .collect(Collectors.toSet());

            resume.setSkills(skills);
        }

        return resumeRepository.save(resume);
    }
}
