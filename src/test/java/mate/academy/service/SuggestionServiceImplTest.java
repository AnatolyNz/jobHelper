package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import mate.academy.service.impl.SuggestionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SuggestionServiceImplTest {

    private final SuggestionServiceImpl suggestionService = new SuggestionServiceImpl();

    @Test
    @DisplayName("Should provide action verb suggestions")
    void suggestVerbs_ShouldSuggestMissingVerbs() {
        String text = "developed managed";
        String result = suggestionService.suggestVerbs(text);
        assertTrue(result.contains("led"));
        assertTrue(result.contains("implemented"));
    }

    @Test
    @DisplayName("Should analyze soft skills")
    void inferSoftSkills_ShouldDetectSoftSkills() {
        String text = "communication teamwork";
        String result = suggestionService.inferSoftSkills(text);
        assertTrue(result.contains("communication"));
        assertTrue(result.contains("teamwork"));
    }

    @Test
    @DisplayName("Should estimate tone correctly")
    void estimateTone_ShouldReturnPositiveNegativeNeutral() {
        assertEquals("Positive", suggestionService.estimateTone("success achieved"));
        assertEquals("Negative", suggestionService.estimateTone("problem failed"));
        assertEquals("Neutral", suggestionService.estimateTone("random text"));
    }
}
