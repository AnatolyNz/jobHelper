package mate.academy.service.impl;

import java.text.Normalizer;
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
    private static final List<String> REQUIRED_SECTIONS_UA = List.of(
            "досвід", "досвід роботи", "освіта", "навички", "контакти", "ціль", "про себе"
    );
    private static final List<String> REQUIRED_SECTIONS_EN = List.of(
            "experience", "education", "skills", "contact", "summary"
    );

    private static final List<String> USEFUL_KEYWORDS = List.of(
            "developed", "managed", "designed", "led", "achieved", "implemented", "built",
            "created", "розроблено", "керовано", "проектовано", "керував", "досягнуто",
            "впроваджено", "створено", "frontend", "html", "css", "javascript", "react"
    );

    private final ResumeRepository resumeRepository;
    private final AtsEvaluationRepository atsEvaluationRepository;
    private final ResumeParserService resumeParserService;

    @Override
    public AtsEvaluation evaluateResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        String resumeText = resumeParserService.extractTextFromResume(resume);
        String cleanedText = normalizeText(resumeText);

        boolean isUkrainian = cleanedText.matches(".*[а-яіїєґ].*");
        List<String> sections = isUkrainian ? REQUIRED_SECTIONS_UA : REQUIRED_SECTIONS_EN;

        int sectionMatches = 0;
        int keywordMatches = 0;

        for (String section : sections) {
            if (cleanedText.contains(section)) {
                sectionMatches++;
            }
        }

        for (String keyword : USEFUL_KEYWORDS) {
            if (cleanedText.contains(keyword)) {
                keywordMatches++;
            }
        }

        double sectionScore = (sectionMatches / (double) sections.size()) * 50;
        double keywordScore = Math.min(keywordMatches * 5, 50);
        double totalScore = sectionScore + keywordScore;

        String feedback;
        if (totalScore >= 80) {
            feedback = isUkrainian
                    ? "Чудове резюме! Добре структуроване та зручне для ATS."
                    : "Excellent resume! Well-structured and ATS-friendly.";
        } else if (totalScore >= 50) {
            feedback = isUkrainian
                    ? "Резюме непогане, але потребує більше деталей або структури."
                    : "Good resume, but it could use more structure or detail.";
        } else {
            feedback = isUkrainian
                    ? "У резюме бракує ключових розділів та "
                    + "ключових слів. Покращте форматування та зміст."
                    : "Your resume lacks important sections or keywords. "
                    + "Improve structure and clarity.";
        }

        AtsEvaluation evaluation = new AtsEvaluation();
        evaluation.setResume(resume);
        evaluation.setScore(totalScore);
        evaluation.setFeedback(feedback);

        return atsEvaluationRepository.save(evaluation);
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKC);

        normalized = normalized
                .toLowerCase()
                .replaceAll("[-_]", " ")
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return normalized;
    }
}
