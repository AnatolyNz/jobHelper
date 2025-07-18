package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Resume;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.ResumeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {
    private final ResumeRepository resumeRepository;

    @Override
    public Resume findById(Long id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + id));
    }
}
