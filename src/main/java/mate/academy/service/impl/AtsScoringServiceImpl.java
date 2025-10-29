package mate.academy.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AtsScoreResult;
import mate.academy.service.AtsScoringService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtsScoringServiceImpl implements AtsScoringService {

    private static final List<String> SECTIONS_EN = List.of(
            "experience", "education", "skills", "contact", "summary",
            "projects", "previous experience", "additional education", "achievements"
    );

    private static final List<String> SECTIONS_UA = List.of(
            "досвід", "освіта", "навички", "контакт", "резюме",
            "проекти", "попередній досвід", "додаткова освіта", "досягнення"
    );

    private static final List<String> KEYWORDS_EN = List.of(
            "developed", "led", "managed", "achieved", "designed", "built",
            "implemented", "tested", "deployed", "contributed", "prepared", "participated"
    );

    private static final List<String> KEYWORDS_UA = List.of(
            "розроблено", "керовано", "досягнуто", "спроектовано", "створено", "збудовано",
            "впроваджено", "протестовано", "розгорнуто", "підготовлено", "брав участь"
    );

    @Override
    public double score(String text) {
        return scoreDetailed(text).totalScore();
    }

    @Override
    public AtsScoreResult scoreDetailed(String text) {
        // normalize text: lowercase, remove bullets, pipes, punctuation, extra spaces
        String normalized = text.toLowerCase()
                .replace("", " ")
                .replace("|", " ")
                .replaceAll("[^a-zA-Zа-яА-Я0-9\\s]", " ")
                .replaceAll("\\s+", " ");

        // count matches
        long secEn = SECTIONS_EN.stream().filter(normalized::contains).count();
        long secUa = SECTIONS_UA.stream().filter(normalized::contains).count();
        long keyEn = KEYWORDS_EN.stream().filter(normalized::contains).count();
        long keyUa = KEYWORDS_UA.stream().filter(normalized::contains).count();

        String language = detectLanguage(secEn, secUa, keyEn, keyUa);

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

        double sectionFrac = getFraction(language, secEn, secUa,
                SECTIONS_EN.size(), SECTIONS_UA.size());
        double keywordFrac = getFraction(language, keyEn, keyUa,
                KEYWORDS_EN.size(), KEYWORDS_UA.size());

        double sectionScore = sectionFrac * 50.0;
        double keywordScore = Math.min(keywordFrac * 50.0, 50.0);
        double totalScore = sectionScore + keywordScore;

        List<String> foundSections = baseSections.stream()
                .filter(normalized::contains)
                .collect(Collectors.toList());
        List<String> missingSections = baseSections.stream()
                .filter(s -> !normalized.contains(s))
                .collect(Collectors.toList());

        List<String> foundKeywords = baseKeywords.stream()
                .filter(normalized::contains)
                .collect(Collectors.toList());
        List<String> missingKeywords = baseKeywords.stream()
                .filter(k -> !normalized.contains(k))
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
        return a.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
