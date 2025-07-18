package mate.academy.mapper;

import mate.academy.dto.AtsEvaluationDto;
import mate.academy.model.AtsEvaluation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AtsEvaluationMapper {

    @Mapping(source = "resume.id", target = "resumeId")
    AtsEvaluationDto toDto(AtsEvaluation eval);
}
