package mate.academy.mapper;

import mate.academy.dto.JobMatchDto;
import mate.academy.model.JobMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMatchMapper {

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle")
    @Mapping(source = "resume.id", target = "resumeId")
    JobMatchDto toDto(JobMatch match);
}
