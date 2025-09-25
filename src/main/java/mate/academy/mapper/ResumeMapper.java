package mate.academy.mapper;

import java.util.List;
import java.util.Set;
import mate.academy.dto.ResumeDto;
import mate.academy.model.Resume;
import mate.academy.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "skills", target = "skillNames", qualifiedByName = "skillsToNames")
    @Mapping(source = "extractedSkills", target = "extractedSkills")
    ResumeDto toDto(Resume resume);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "skills", ignore = true)
    Resume toEntity(ResumeDto resumeDto);

    @Named("skillsToNames")
    default List<String> skillsToNames(Set<Skill> skills) {
        return skills.stream()
                .map(Skill::getName)
                .toList();
    }
}
