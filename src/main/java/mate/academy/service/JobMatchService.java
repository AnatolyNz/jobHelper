package mate.academy.service;

import java.util.List;
import mate.academy.model.JobMatch;

public interface JobMatchService {
    List<JobMatch> findMatchesForResume(Long resumeId);
}
