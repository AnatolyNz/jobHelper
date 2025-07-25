package mate.academy.service;

import mate.academy.dto.ResumeDto;
import mate.academy.model.Resume;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService {
    Resume findById(Long id);

    Resume save(MultipartFile file, Long userId);

    Resume save(ResumeDto resumeDto);
}
