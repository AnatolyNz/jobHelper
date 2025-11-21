package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import mate.academy.model.AiAnalyzedResume;
import mate.academy.model.Resume;
import mate.academy.service.impl.ResumeRuleEngineImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResumeRuleEngineImplTest {

    @Mock private AtsScoringService atsScoringService;
    @Mock private GrammarCheckService grammarCheckService;
    @Mock private SectionStructureService sectionStructureService;
    @Mock private SuggestionService suggestionService;

    @InjectMocks private ResumeRuleEngineImpl ruleEngine;

    @Test
    @DisplayName("Should evaluate resume and return AiAnalyzedResume")
    void evaluate_ShouldReturnAiAnalyzedResume() {
        Resume resume = new Resume();
        String text = "sample resume text";

        when(atsScoringService.score(text)).thenReturn(80.0);
        when(grammarCheckService.checkGrammar(text)).thenReturn("grammar OK");
        when(sectionStructureService.analyze(text)).thenReturn("structure OK");
        when(suggestionService.analyze(text)).thenReturn("suggestions OK");

        AiAnalyzedResume result = ruleEngine.evaluate(resume, text);

        assertEquals(resume, result.getResume());
        assertEquals(80.0, result.getAtsScore());
        assertEquals("grammar OK", result.getGrammarFeedback());
        assertEquals("structure OK", result.getStructureFeedback());
        assertEquals("suggestions OK", result.getActionVerbSuggestions());
    }
}
