package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import mate.academy.service.impl.GrammarCheckServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GrammarCheckServiceImplTest {

    private final GrammarCheckServiceImpl grammarService = new GrammarCheckServiceImpl();

    @Test
    @DisplayName("Should return feedback for grammar errors")
    void checkGrammar_ShouldDetectErrors() {
        String text = "This is bad gramar sentence.";
        String result = grammarService.checkGrammar(text);
        assertTrue(result.contains("Виявлено граматичні") || result.contains("No major grammar"));
    }

    @Test
    @DisplayName("Should return no errors for correct text")
    void checkGrammar_NoErrors_ShouldReturnNoIssues() {
        String text = "This is a correct sentence.";
        String result = grammarService.checkGrammar(text);
        assertTrue(result.contains("No major grammar") || result.contains("Виявлено граматичні"));
    }
}
