package mate.academy.dto;

import java.time.LocalDateTime;

public record AiCustomizedResumeDto(
        Long id,
        Long resumeId,
        Long jobId,
        String content,
        LocalDateTime generatedAt
) {}
