package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.AtsEvaluation;
import mate.academy.model.Resume;
import mate.academy.repository.AtsEvaluationRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.AtsEvaluationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtsEvaluationServiceImpl implements AtsEvaluationService {
    private final ResumeRepository resumeRepository;
    private final AtsEvaluationRepository atsEvaluationRepository;

    @Override
    public AtsEvaluation evaluateResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        AtsEvaluation evaluation = new AtsEvaluation();
        evaluation.setResume(resume);
        evaluation.setScore(85);
        evaluation.setFeedback("Looks good for most ATS systems.");
        return atsEvaluationRepository.save(evaluation);
    }
}
