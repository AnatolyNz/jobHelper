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
            "впроваджено", "протестовано", "розгорнуто", "підготовлено", "брав", "участь"
    );

    @Override
    public double score(String text) {
        return scoreDetailed(text).totalScore();
    }

    @Override
    public AtsScoreResult scoreDetailed(String text) {
        if (text == null || text.isBlank()) {
            return emptyResult();
        }

        // Normalize lowercase text
        String normalized = normalize(text);

        // unique words set
        Set<String> words = Stream.of(normalized.split("\\s+"))
                .collect(Collectors.toSet());

        // Count English
        long secEn = countContains(normalized, SECTIONS_EN);
        long keyEn = countWords(words, KEYWORDS_EN);

        // Count Ukrainian
        long secUa = countContains(normalized, SECTIONS_UA);
        long keyUa = countWords(words, KEYWORDS_UA);

        // Detect language
        String lang = detectLanguage(secEn, secUa, keyEn, keyUa);

        // Pick base lists according to detected language
        List<String> baseSections = switch (lang) {
            case "UA" -> SECTIONS_UA;
            case "MIXED" -> mergeLists(SECTIONS_EN, SECTIONS_UA);
            default -> SECTIONS_EN;
        };

        List<String> baseKeywords = switch (lang) {
            case "UA" -> KEYWORDS_UA;
            case "MIXED" -> mergeLists(KEYWORDS_EN, KEYWORDS_UA);
            default -> KEYWORDS_EN;
        };

        // Fractions
        double sectionFrac = getFraction(lang, secEn, secUa,
                SECTIONS_EN.size(), SECTIONS_UA.size());
        double keywordFrac = getFraction(lang, keyEn, keyUa,
                KEYWORDS_EN.size(), KEYWORDS_UA.size());

        double sectionScore = sectionFrac * 50;
        double keywordScore = Math.min(keywordFrac * 50, 50);
        double totalScore = sectionScore + keywordScore;

        // Found / Missing (always lowercase!)
        List<String> foundSections = baseSections.stream()
                .filter(s -> normalized.contains(s))
                .collect(Collectors.toList());

        List<String> missingSections = baseSections.stream()
                .filter(s -> !normalized.contains(s))
                .collect(Collectors.toList());

        List<String> foundKeywords = baseKeywords.stream()
                .filter(words::contains)
                .collect(Collectors.toList());

        List<String> missingKeywords = baseKeywords.stream()
                .filter(k -> !words.contains(k))
                .collect(Collectors.toList());

        return new AtsScoreResult(
                totalScore,
                sectionScore,
                keywordScore,
                foundSections,
                missingSections,
                foundKeywords,
                missingKeywords,
                lang
        );
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replace("", " ")
                .replace("|", " ")
                .replaceAll("[^a-zа-яіїєґ0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private AtsScoreResult emptyResult() {
        return new AtsScoreResult(
                0, 0, 0,
                List.of(), List.of(),
                List.of(), List.of(),
                "UNKNOWN"
        );
    }

    private long countContains(String text, List<String> patterns) {
        return patterns.stream()
                .filter(text::contains)
                .count();
    }

    private long countWords(Set<String> words, List<String> dict) {
        return dict.stream()
                .filter(words::contains)
                .count();
    }

    private String detectLanguage(long secEn, long secUa, long keyEn, long keyUa) {
        boolean en = (secEn + keyEn) > 0;
        boolean ua = (secUa + keyUa) > 0;

        if (en && !ua) {
            return "EN";
        }
        if (!en && ua) {
            return "UA";
        }
        if (en && ua) {
            return "MIXED";
        }
        return "UNKNOWN";
    }

    private double getFraction(String lang, long enCount, long uaCount,
                               int sizeEn, int sizeUa) {
        return switch (lang) {
            case "EN" -> enCount / (double) sizeEn;
            case "UA" -> uaCount / (double) sizeUa;
            case "MIXED" -> ((enCount / (double) sizeEn) + (uaCount / (double) sizeUa)) / 2.0;
            default -> 0.0;
        };
    }

    private List<String> mergeLists(List<String> a, List<String> b) {
        return Stream.concat(a.stream(), b.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
