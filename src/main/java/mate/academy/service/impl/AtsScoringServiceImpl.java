package mate.academy.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AtsScoreResult;
import mate.academy.service.AtsScoringService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtsScoringServiceImpl implements AtsScoringService {

    private static final List<String> SECTIONS_EN = List.of(
            "Experience", "Education", "Skills", "Contact", "Summary",
            "Projects", "Previous Experience", "Additional Education", "Achievements"
    );

    private static final List<String> SECTIONS_UA = List.of(
            "Досвід", "Освіта", "Навички", "Контакт", "Резюме",
            "Проекти", "Попередній досвід", "Додаткова освіта", "Досягнення"
    );

    private static final List<String> KEYWORDS_EN = List.of(
            "Developed", "Led", "Managed", "Achieved", "Designed", "Built",
            "Implemented", "Tested", "Deployed", "Contributed", "Prepared", "Participated"
    );

    private static final List<String> KEYWORDS_UA = List.of(
            "Розроблено", "Керовано", "Досягнуто", "Спроектовано", "Створено", "Збудовано",
            "Впроваджено", "Протестовано", "Розгорнуто", "Підготовлено", "Брав участь"
    );

    @Override
    public double score(String text) {
        return scoreDetailed(text).totalScore();
    }

    @Override
    public AtsScoreResult scoreDetailed(String text) {
        if (text == null || text.isBlank()) {
            return new AtsScoreResult(0, 0,
                    0, List.of(), List.of(),
                    List.of(), List.of(), "UNKNOWN");
        }

        // ✅ Normalize text
        String normalized = text.toLowerCase()
                .replace("", " ")
                .replace("|", " ")
                .replaceAll("[^a-zа-я0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        // ✅ Use Stream + Collectors.toSet() (fixes IllegalArgumentException)
        Set<String> words = Stream.of(normalized.split("\\s+"))
                .collect(Collectors.toSet());

        // Count sections and keywords
        long secEn = SECTIONS_EN.stream()
                .map(String::toLowerCase)
                .filter(normalized::contains)
                .count();

        long secUa = SECTIONS_UA.stream()
                .map(String::toLowerCase)
                .filter(normalized::contains)
                .count();

        long keyEn = KEYWORDS_EN.stream()
                .map(String::toLowerCase)
                .filter(words::contains)
                .count();

        long keyUa = KEYWORDS_UA.stream()
                .map(String::toLowerCase)
                .filter(words::contains)
                .count();

        // Detect language
        String language = detectLanguage(secEn, secUa, keyEn, keyUa);

        // Choose base lists depending on language
        List<String> baseSections;
        List<String> baseKeywords;

        switch (language) {
            case "UA" -> {
                baseSections = SECTIONS_UA;
                baseKeywords = KEYWORDS_UA;
            }
            case "MIXED" -> {
                baseSections = mergeLists(SECTIONS_EN, SECTIONS_UA);
                baseKeywords = mergeLists(KEYWORDS_EN, KEYWORDS_UA);
            }
            default -> {
                baseSections = SECTIONS_EN;
                baseKeywords = KEYWORDS_EN;
            }
        }

        // Calculate score fractions
        double sectionFrac = getFraction(language, secEn, secUa,
                SECTIONS_EN.size(), SECTIONS_UA.size());
        double keywordFrac = getFraction(language, keyEn, keyUa,
                KEYWORDS_EN.size(), KEYWORDS_UA.size());

        double sectionScore = sectionFrac * 50.0;
        double keywordScore = Math.min(keywordFrac * 50.0, 50.0);
        double totalScore = sectionScore + keywordScore;

        // Build found/missing lists
        List<String> foundSections = baseSections.stream()
                .filter(s -> normalized.contains(s.toLowerCase()))
                .collect(Collectors.toList());

        List<String> missingSections = baseSections.stream()
                .filter(s -> !normalized.contains(s.toLowerCase()))
                .collect(Collectors.toList());

        List<String> foundKeywords = baseKeywords.stream()
                .filter(k -> words.contains(k.toLowerCase()))
                .collect(Collectors.toList());

        List<String> missingKeywords = baseKeywords.stream()
                .filter(k -> !words.contains(k.toLowerCase()))
                .collect(Collectors.toList());

        return new AtsScoreResult(
                totalScore,
                sectionScore,
                keywordScore,
                foundSections,
                missingSections,
                foundKeywords,
                missingKeywords,
                language
        );
    }

    private String detectLanguage(long secEn, long secUa, long keyEn, long keyUa) {
        boolean hasEn = (secEn + keyEn) > 0;
        boolean hasUa = (secUa + keyUa) > 0;

        if (hasEn && !hasUa) {
            return "EN";
        }
        if (!hasEn && hasUa) {
            return "UA";
        }
        if (hasEn && hasUa) {
            return "MIXED";
        }
        return "UNKNOWN";
    }

    private double getFraction(String language, long enCount,
                               long uaCount, int enSize, int uaSize) {
        return switch (language) {
            case "EN" -> enCount / (double) enSize;
            case "UA" -> uaCount / (double) uaSize;
            case "MIXED" -> ((enCount / (double) enSize) + (uaCount / (double) uaSize)) / 2.0;
            default -> 0.0;
        };
    }

    private List<String> mergeLists(List<String> a, List<String> b) {
        return Stream.concat(a.stream(), b.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
