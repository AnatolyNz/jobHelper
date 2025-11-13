package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.model.AiAnalyzedResume;
import mate.academy.model.Resume;
import mate.academy.service.AtsScoringService;
import mate.academy.service.GrammarCheckService;
import mate.academy.service.ResumeRuleEngine;
import mate.academy.service.SectionStructureService;
import mate.academy.service.SuggestionService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeRuleEngineImpl implements ResumeRuleEngine {

    private final AtsScoringService atsScoringService;
    private final GrammarCheckService grammarCheckService;
    private final SectionStructureService sectionStructureService;
    private final SuggestionService suggestionService;

    @Override
    public AiAnalyzedResume evaluate(Resume resume, String resumeText) {
        double atsScore = atsScoringService.score(resumeText);
        String grammarFeedback = grammarCheckService.checkGrammar(resumeText);
        String structureFeedback = sectionStructureService.analyze(resumeText);
        String suggestions = suggestionService.analyze(resumeText);

        AiAnalyzedResume result = new AiAnalyzedResume();
        result.setResume(resume);
        result.setAtsScore(atsScore);
        result.setGrammarFeedback(grammarFeedback);
        result.setStructureFeedback(structureFeedback);
        result.setToneFeedback("N/A");
        result.setActionVerbSuggestions(suggestions);
        result.setQuantificationSuggestions(suggestions);
        result.setSoftSkillsInferred(suggestions);

        return result;
    }

    @Override
    public AiAnalyzedResume combine(AiAnalyzedResume resume) {
        return resume;
    }
}
