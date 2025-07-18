package mate.academy.mapper;

import java.util.Arrays;
import java.util.List;
import mate.academy.dto.SurveyDto;
import mate.academy.model.Survey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "jobPreferences", target = "jobPreferences",
            qualifiedByName = "splitJobPreferences")
    SurveyDto toDto(Survey survey);

    @Named("splitJobPreferences")
    default List<String> splitJobPreferences(String value) {
        return value == null || value.isBlank()
                ? List.of()
                : Arrays.stream(value.split(","))
                .map(String::trim)
                .toList();
    }
}
