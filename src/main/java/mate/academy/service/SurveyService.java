package mate.academy.service;

import mate.academy.dto.SurveyRequestDto;
import mate.academy.model.Survey;

public interface SurveyService {
    Survey save(SurveyRequestDto dto);

    Survey findById(Long id);
}
