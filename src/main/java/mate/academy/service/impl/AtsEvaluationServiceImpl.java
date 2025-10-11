package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.AtsEvaluation;
import mate.academy.model.Resume;
import mate.academy.repository.AtsEvaluationRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.AtsEvaluationService;
import mate.academy.service.ResumeParserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtsEvaluationServiceImpl implements AtsEvaluationService {
    private static final List<String> requiredSections = List.of(
            "experience", "education", "skills", "contact", "summary"
    );

    private static final List<String> usefulKeywords = List.of(
            "developed", "managed", "designed", "led", "achieved", "implemented", "built",
            "created", "розроблено", "керовано", "проектовано", "керував", "досягнуто",
            "впроваджено", "створено"
    );
    private final ResumeRepository resumeRepository;
    private final AtsEvaluationRepository atsEvaluationRepository;
    private final ResumeParserService resumeParserService;

    public AtsEvaluation evaluateResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        String resumeText = resumeParserService.extractTextFromResume(resume);

        int sectionMatches = 0;
        int keywordMatches = 0;

        for (String section : requiredSections) {
            if (resumeText.toLowerCase().contains(section)) {
                sectionMatches++;
            }
        }

        for (String keyword : usefulKeywords) {
            if (resumeText.toLowerCase().contains(keyword)) {
                keywordMatches++;
            }
        }

        double sectionScore = (sectionMatches / (double) requiredSections.size()) * 50;
        double keywordScore = Math.min(keywordMatches * 5, 50);

        double totalScore = sectionScore + keywordScore;

        String feedback;
        if (totalScore >= 80) {
            feedback = "Чудове резюме! Добре структуроване та зручне для ATS.";
        } else if (totalScore >= 50) {
            feedback = "Резюме непогане, але потребує більше деталей або структури.";
        } else {
            feedback = "У резюме бракує ключових розділів та ключових слів. "
                    + "Покращте форматування та зміст.";
        }

        AtsEvaluation evaluation = new AtsEvaluation();
        evaluation.setResume(resume);
        evaluation.setScore(totalScore);
        evaluation.setFeedback(feedback);

        return atsEvaluationRepository.save(evaluation);
    }
}
