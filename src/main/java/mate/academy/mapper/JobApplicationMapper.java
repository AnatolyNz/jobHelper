package mate.academy.mapper;

import java.util.List;
import java.util.stream.Collectors;
import mate.academy.dto.JobApplicationDto;
import mate.academy.model.JobApplication;
import mate.academy.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "job.title", target = "title")
    @Mapping(source = "job.description", target = "description")
    @Mapping(source = "job.company", target = "company")
    @Mapping(source = "job.location", target = "location")
    @Mapping(source = "job.salary", target = "salary")
    @Mapping(source = "job.workFormat", target = "workFormat")
    @Mapping(source = "job.requiredSkills", target =
            "requiredSkills", qualifiedByName = "skillsToNames")
    @Mapping(source = "status.status", target = "status")
    JobApplicationDto toDto(JobApplication application);

    @Mapping(target = "job", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", ignore = true)
    JobApplication toEntity(JobApplicationDto dto);

    @Named("skillsToNames")
    default List<String> mapSkillsToNames(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return List.of();
        }
        return skills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }
}
