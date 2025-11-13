package mate.academy.service.impl;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import mate.academy.service.SectionStructureService;
import org.springframework.stereotype.Service;

@Service
public class SectionStructureServiceImpl implements SectionStructureService {

    // English sections
    private static final List<String> SECTIONS_EN = List.of(
            "summary", "objective", "experience", "education", "skills",
            "projects", "certifications", "contact"
    );

    // Ukrainian sections
    private static final List<String> SECTIONS_UA = List.of(
            "резюме", "мета", "досвід", "освіта", "навички",
            "проекти", "сертифікати", "контакт"
    );

    // Mapping English -> Ukrainian for feedback
    private static final Map<String, String> SECTION_TRANSLATIONS = Map.of(
            "summary", "резюме",
            "objective", "мета",
            "experience", "досвід",
            "education", "освіта",
            "skills", "навички",
            "projects", "проекти",
            "certifications", "сертифікати",
            "contact", "контакт"
    );

    // Patterns to detect sections (English + Ukrainian)
    private static final Map<String, Pattern> SECTION_PATTERNS = new HashMap<>();

    static {
        for (String section : SECTIONS_EN) {
            SECTION_PATTERNS.put(section, Pattern.compile("(?i)^\\s*"
                    + section + "\\b.*", Pattern.MULTILINE));
        }
        for (String section : SECTIONS_UA) {
            SECTION_PATTERNS.put(section, Pattern.compile("(?i)^\\s*"
                    + section + "\\b.*", Pattern.MULTILINE));
        }
    }

    @Override
    public String analyze(String resumeText) {
        if (resumeText == null || resumeText.isBlank()) {
            return "📌 Текст резюме порожній або недоступний.";
        }

        Set<String> foundSections = new LinkedHashSet<>();

        // Detect all sections
        for (Map.Entry<String, Pattern> entry : SECTION_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(resumeText).find()) {
                foundSections.add(entry.getKey());
            }
        }

        // Determine missing sections (based on English base list)
        List<String> missingSections = SECTIONS_EN.stream()
                .filter(s -> !foundSections.contains(s) && !foundSections
                        .contains(SECTION_TRANSLATIONS.get(s)))
                .map(s -> SECTION_TRANSLATIONS.getOrDefault(s, s))
                .collect(Collectors.toList());

        // Translate found sections for feedback
        List<String> translatedFound = foundSections.stream()
                .map(s -> SECTION_TRANSLATIONS.getOrDefault(s, s))
                .collect(Collectors.toList());

        StringBuilder feedback = new StringBuilder("📄 Аналіз розділу резюме:\n");

        if (!translatedFound.isEmpty()) {
            feedback.append("✅ Знайдені розділи: ")
                    .append(String.join(", ", translatedFound))
                    .append("\n");
        }

        if (!missingSections.isEmpty()) {
            feedback.append("❌ Відсутні розділи: ")
                    .append(String.join(", ", missingSections))
                    .append("\n");
        }

        if (missingSections.isEmpty() && translatedFound.size() >= 5) {
            feedback.append("🎯 Структура резюме повна та добре організована.");
        } else {
            feedback.append("📌 Розгляньте можливість "
                    + "додавання відсутніх розділів для покращення структури.");
        }

        return feedback.toString();
    }
}
