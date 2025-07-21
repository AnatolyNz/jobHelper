package mate.academy.service;

import mate.academy.dto.ResumeDto;
import mate.academy.model.Resume;

public interface ResumeService {
    Resume findById(Long id);

    Resume save(ResumeDto resumeDto);
}
