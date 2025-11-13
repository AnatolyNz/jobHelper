package mate.academy.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AiAnalyzedResumeDto;
import mate.academy.dto.AtsScoreResult;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.ResumeAnalysisException;
import mate.academy.model.AiAnalyzedResume;
import mate.academy.model.Resume;
import mate.academy.repository.AiAnalyzedResumeRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.AiResumeAnalyzerService;
import mate.academy.service.AtsScoringService;
import mate.academy.service.GrammarCheckService;
import mate.academy.service.ResumeParserService;
import mate.academy.service.ResumeRuleEngine;
import mate.academy.service.SectionStructureService;
import mate.academy.service.SuggestionService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiResumeAnalyzerServiceImpl implements AiResumeAnalyzerService {

    private final ResumeRepository resumeRepository;
    private final AiAnalyzedResumeRepository analyzedResumeRepository;
    private final ResumeParserService resumeParserService;
    private final AtsScoringService atsScoringService;
    private final GrammarCheckService grammarCheckService;
    private final SectionStructureService structureService;
    private final SuggestionService suggestionService;
    private final ResumeRuleEngine ruleEngine;

    @Override
    public AiAnalyzedResumeDto analyzeResume(Long resumeId) {
        // 1. Fetch resume
        Resume resume = resumeRepository.findByIdWithAllDetails(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        // 2. Extract text
        String text = resumeParserService.extractTextFromFileData(resume.getFileData());

        // 3. Get detailed ATS scoring (sections + keywords)
        var atsResult = atsScoringService.scoreDetailed(text);

        // 4. Create AI-analyzed entity
        AiAnalyzedResume result = new AiAnalyzedResume();
        result.setResume(resume);
        result.setAtsScore(adjustAtsScore(atsResult)); // boost to ~70%
        result.setGrammarFeedback(grammarCheckService.checkGrammar(text));
        result.setStructureFeedback(structureService.analyze(text));
        result.setActionVerbSuggestions(normalizeAndJoin(suggestionService.suggestVerbs(text)));
        result.setQuantificationSuggestions(normalizeAndJoin(suggestionService
                .suggestQuantification(text)));
        result.setSoftSkillsInferred(normalizeAndJoin(suggestionService.inferSoftSkills(text)));
        result.setToneFeedback(suggestionService.estimateTone(text));

        // 5. Apply rule engine
        result = ruleEngine.combine(result);
        if (result == null) {
            throw new ResumeAnalysisException("Rule engine "
                    + "returned null analyzed resume");
        }

        // 6. Persist analyzed resume
        result = analyzedResumeRepository.save(result);

        // 7. Map to DTO
        AiAnalyzedResumeDto dto = new AiAnalyzedResumeDto();
        dto.setId(result.getId());
        dto.setResumeFileName(resume.getFileName());
        dto.setAtsScore(String.valueOf(result.getAtsScore()));
        dto.setGrammarFeedback(result.getGrammarFeedback());
        dto.setStructureFeedback(result.getStructureFeedback());
        dto.setActionVerbSuggestions(result.getActionVerbSuggestions());
        dto.setQuantificationSuggestions(result.getQuantificationSuggestions());
        dto.setSoftSkillsInferred(result.getSoftSkillsInferred());
        dto.setToneFeedback(result.getToneFeedback());

        return dto;
    }

    /**
     * Boost raw ATS score from 0–50 (sections) + 0–50 (keywords)
     * to produce realistic evaluation (~70% for partially complete resumes)
     */
    private double adjustAtsScore(AtsScoreResult ats) {
        double raw = ats.totalScore();

        // If score is very low (<20%), boost for human-readable evaluation
        if (raw < 30.0) {
            return 70.0; // minimal acceptable ATS evaluation
        }

        // Otherwise, use ATS fraction + small boost (up to +10%)
        double boost = Math.min(10.0, 100.0 - raw);
        return raw + boost;
    }

    private String normalizeAndJoin(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        return List.of(input.split("\\s*,\\s*")).stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.joining(", "));
    }
}
