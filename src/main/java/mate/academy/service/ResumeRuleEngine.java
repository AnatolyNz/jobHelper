package mate.academy.service;

import mate.academy.model.AiAnalyzedResume;
import mate.academy.model.Resume;

public interface ResumeRuleEngine {
    AiAnalyzedResume evaluate(Resume resume, String resumeText);

    AiAnalyzedResume combine(AiAnalyzedResume resume);
}
