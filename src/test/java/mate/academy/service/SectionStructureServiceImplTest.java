package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import mate.academy.service.impl.SectionStructureServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionStructureServiceImplTest {

    private final SectionStructureServiceImpl sectionService = new SectionStructureServiceImpl();

    @Test
    @DisplayName("Should detect all sections in English")
    void analyze_ShouldDetectSectionsEnglish() {
        String text = "Summary\nExperience\nEducation\nSkills\nProjects\nCertifications\nContact";
        String result = sectionService.analyze(text);
        assertTrue(result.contains("✅ Знайдені розділи"));
    }

    @Test
    @DisplayName("Should detect missing sections")
    void analyze_ShouldDetectMissingSections() {
        String text = "Summary\nExperience";
        String result = sectionService.analyze(text);
        assertTrue(result.contains("❌ Відсутні розділи"));
    }
}
