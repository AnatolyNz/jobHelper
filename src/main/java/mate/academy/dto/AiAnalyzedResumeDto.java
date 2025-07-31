package mate.academy.dto;

import lombok.Data;

@Data
public class AiAnalyzedResumeDto {
    private Long id;
    private String resumeFileName;
    private String atsScore;
    private String grammarFeedback;
    private String structureFeedback;
    private String actionVerbSuggestions;
    private String quantificationSuggestions;
    private String softSkillsInferred;
    private String toneFeedback;
}
