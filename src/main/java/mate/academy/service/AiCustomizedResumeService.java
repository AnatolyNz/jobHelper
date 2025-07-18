package mate.academy.service;

import mate.academy.model.AiCustomizedResume;

public interface AiCustomizedResumeService {
    AiCustomizedResume generateCustomizedResume(Long resumeId, Long jobId);
}
