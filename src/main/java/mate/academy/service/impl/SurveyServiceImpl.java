package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.dto.SurveyRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.Survey;
import mate.academy.model.User;
import mate.academy.repository.SurveyRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.SurveyService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    @Override
    public Survey save(SurveyRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found: "
                        + dto.getUserId()));

        Survey survey = new Survey();
        survey.setUser(user);
        survey.setExperienceLevel(dto.getExperienceLevel());
        survey.setJobPreferences(String.join(",", dto.getJobPreferences()));
        survey.setLocationPreference(dto.getLocationPreference());

        return surveyRepository.save(survey);
    }

    @Override
    public Survey findById(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Survey not found: " + id));
    }
}
