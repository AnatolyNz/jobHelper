package mate.academy.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.AiAnalyzedResumeDto;
import mate.academy.dto.ResumeDto;
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
        Resume resume = resumeRepository.findWithUserById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));

        ResumeDto resumeDto = new ResumeDto(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getUser().getEmail(),
                resume.getUser().getFirstName(),
                resume.getUser().getLastName(),
                resume.getFileData(),
                resume.getUser().getId(),
                resume.getExtractedSkills() == null
                        ? Collections.emptyList() : new ArrayList<>(resume.getExtractedSkills()),
                Collections.emptyList()
        );

        String text = resumeParserService.extractTextFromFileData(resumeDto.getFileData());

        Resume resumeEntity = new Resume();
        resumeEntity.setId(resumeDto.getId());

        AiAnalyzedResume result = new AiAnalyzedResume();
        result.setResume(resumeEntity);
        result.setAtsScore(atsScoringService.score(text));
        result.setGrammarFeedback(grammarCheckService.checkGrammar(text));
        result.setStructureFeedback(structureService.analyze(text));
        result.setActionVerbSuggestions(suggestionService.suggestVerbs(text));
        result.setQuantificationSuggestions(suggestionService.suggestQuantification(text));
        result.setSoftSkillsInferred(suggestionService.inferSoftSkills(text));
        result.setToneFeedback(suggestionService.estimateTone(text));

        // Combine analysis results with rule engine
        result = ruleEngine.combine(result);
        if (result == null) {
            throw new ResumeAnalysisException("Rule engine returned null analyzed resume");
        }

        result = analyzedResumeRepository.save(result);

        AiAnalyzedResumeDto dto = new AiAnalyzedResumeDto();
        dto.setId(result.getId());
        dto.setResumeFileName(resumeDto.getFileName());
        dto.setAtsScore(String.valueOf(result.getAtsScore()));
        dto.setGrammarFeedback(result.getGrammarFeedback());
        dto.setStructureFeedback(result.getStructureFeedback());
        dto.setActionVerbSuggestions(result.getActionVerbSuggestions());
        dto.setQuantificationSuggestions(result.getQuantificationSuggestions());
        dto.setSoftSkillsInferred(result.getSoftSkillsInferred());
        dto.setToneFeedback(result.getToneFeedback());

        return dto;
    }
}
