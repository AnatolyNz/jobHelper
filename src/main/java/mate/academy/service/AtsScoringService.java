package mate.academy.service;

import mate.academy.dto.AtsScoreResult;

public interface AtsScoringService {
    double score(String resumeText);

    AtsScoreResult scoreDetailed(String resumeText);
}
