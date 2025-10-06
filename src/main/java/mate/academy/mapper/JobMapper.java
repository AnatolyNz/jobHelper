package mate.academy.mapper;

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
    JobDto toDto(Job job);

    @Mapping(source = "requiredSkills", target = "requiredSkills",
            qualifiedByName = "namesToSkills")
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
}
