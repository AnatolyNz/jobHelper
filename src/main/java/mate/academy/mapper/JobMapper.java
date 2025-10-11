package mate.academy.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import mate.academy.dto.JobDto;
import mate.academy.model.Job;
import mate.academy.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(source = "requiredSkills", target = "requiredSkills",
            qualifiedByName = "skillsToNames")
    @Mapping(source = "workFormat", target = "workFormat", qualifiedByName = "enumToString")
    JobDto toDto(Job job);

    @Mapping(source = "requiredSkills", target = "requiredSkills",
            qualifiedByName = "namesToSkills")
    @Mapping(source = "workFormat", target = "workFormat", qualifiedByName = "stringToEnum")
    Job toModel(JobDto jobDto);

    @Named("skillsToNames")
    default List<String> mapSkillsToNames(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        return skills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }

    @Named("namesToSkills")
    default List<Skill> mapNamesToSkills(List<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return List.of();
        }
        return skillNames.stream()
                .map(name -> {
                    Skill skill = new Skill();
                    skill.setName(name);
                    return skill;
                })
                .collect(Collectors.toList());
    }

    @Named("stringToEnum")
    default Job.WorkFormat mapStringToEnum(String workFormat) {
        if (workFormat == null) {
            return null;
        }
        try {
            return Job.WorkFormat.valueOf(workFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid work format: " + workFormat
                    + ". Valid values are: " + Arrays.toString(Job.WorkFormat.values()));
        }
    }

    @Named("enumToString")
    default String mapEnumToString(Job.WorkFormat workFormat) {
        return workFormat != null ? workFormat.name() : null;
    }
}
