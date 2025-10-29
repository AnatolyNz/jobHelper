package mate.academy.dto;

import java.util.List;

public record AtsScoreResult(
        double totalScore,
        double sectionScore,
        double keywordScore,
        List<String> foundSections,
        List<String> missingSections,
        List<String> foundKeywords,
        List<String> missingKeywords,
        String detectedLanguage
) {}
