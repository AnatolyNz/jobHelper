package mate.academy.service;

import mate.academy.dto.AiAnalyzedResumeDto;

public interface AiResumeAnalyzerService {
    AiAnalyzedResumeDto analyzeResume(Long resumeId);
}
