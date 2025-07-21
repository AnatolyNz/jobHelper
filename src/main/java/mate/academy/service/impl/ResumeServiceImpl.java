package mate.academy.service.impl;

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
