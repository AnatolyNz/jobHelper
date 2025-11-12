package mate.academy.service;

import java.util.List;
import mate.academy.dto.JobMatchDto;

public interface JobMatchService {
    List<JobMatchDto> findMatchesForResume(Long resumeId, Long jobId);
}
