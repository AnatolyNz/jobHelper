package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mate.academy.dto.AtsScoreResult;
import mate.academy.service.impl.AtsScoringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AtsScoringServiceTest {

    private AtsScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new AtsScoringServiceImpl();
    }

    @Test
    @DisplayName("Score empty text should be 0")
    void score_emptyText_shouldReturnZero() {
        double score = scoringService.score("");
        assertEquals(0.0, score);
    }

    @Test
    @DisplayName("Score English text with all sections and keywords")
    void score_englishFullText_shouldReturnMaxScore() {
        String text = "Experience Education Skills Contact Summary Projects "
                + "Previous Experience Additional Education Achievements "
                + "Developed Led Managed Achieved Designed Built "
                + "Implemented Tested Deployed Contributed Prepared Participated";

        double score = scoringService.score(text);
        assertEquals(100.0, score);
    }

    @Test
    @DisplayName("Score Ukrainian text with some sections and keywords")
    void score_ukrainianPartialText_shouldReturnPartialScore() {
        String text = "Досвід Освіта Навички Розроблено Керовано Досягнуто";

        AtsScoreResult result = scoringService.scoreDetailed(text);

        assertTrue(result.totalScore() > 0 && result.totalScore() < 100);
        assertEquals("UA", result.detectedLanguage());
        assertTrue(result.foundSections().contains("досвід"));
        assertTrue(result.foundKeywords().contains("розроблено"));
    }

    @Test
    @DisplayName("Score mixed text should detect MIXED language")
    void score_mixedText_shouldDetectMixed() {
        String text = "Experience Освіта Skills Навички Developed Розроблено";

        AtsScoreResult result = scoringService.scoreDetailed(text);

        assertEquals("MIXED", result.detectedLanguage());
        assertTrue(result.foundSections().contains("experience"));
        assertTrue(result.foundSections().contains("освіта"));
        assertTrue(result.foundKeywords().contains("developed"));
        assertTrue(result.foundKeywords().contains("розроблено"));
    }

    @Test
    @DisplayName("Score text with unknown language")
    void score_unknownLanguage_shouldReturnZero() {
        String text = "XYZ ABC 123";

        AtsScoreResult result = scoringService.scoreDetailed(text);

        assertEquals("UNKNOWN", result.detectedLanguage());
        assertEquals(0.0, result.totalScore());
    }

    @Test
    @DisplayName("Found and missing sections and keywords should be correct")
    void score_sectionsAndKeywords_shouldMatch() {
        String text = "Experience Skills Developed";

        AtsScoreResult result = scoringService.scoreDetailed(text);

        assertTrue(result.foundSections().contains("experience"));
        assertTrue(result.missingSections().contains("education"));
        assertTrue(result.foundKeywords().contains("developed"));
        assertTrue(result.missingKeywords().contains("led"));
    }
}
