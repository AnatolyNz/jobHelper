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

    // Common resume section headers (you can expand this list)
    private static final List<String> EXPECTED_SECTIONS = List.of(
            "summary", "objective", "experience", "education", "skills",
            "projects", "certifications", "contact"
    );

    // Map of patterns to detect section headers
    private static final Map<String, Pattern> SECTION_PATTERNS = new HashMap<>();

    static {
        for (String section : EXPECTED_SECTIONS) {
            SECTION_PATTERNS.put(section, Pattern.compile("(?i)^\\s*"
                    + section + "\\b.*", Pattern.MULTILINE));
        }
    }

    @Override
    public String analyze(String resumeText) {
        Set<String> foundSections = new LinkedHashSet<>();

        for (Map.Entry<String, Pattern> entry : SECTION_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(resumeText).find()) {
                foundSections.add(entry.getKey());
            }
        }

        List<String> missing = EXPECTED_SECTIONS.stream()
                .filter(s -> !foundSections.contains(s))
                .collect(Collectors.toList());

        StringBuilder feedback = new StringBuilder("📄 Resume Section Analysis:\n");

        if (!foundSections.isEmpty()) {
            feedback.append("✅ Found sections: ").append(String
                    .join(", ", foundSections)).append("\n");
        }

        if (!missing.isEmpty()) {
            feedback.append("❌ Missing sections: ").append(String.join(", ", missing)).append("\n");
        }

        if (missing.isEmpty() && foundSections.size() >= 5) {
            feedback.append("🎯 Structure looks complete and well-organized.");
        } else {
            feedback.append("📌 Consider adding missing sections to improve structure.");
        }

        return feedback.toString();
    }
}
