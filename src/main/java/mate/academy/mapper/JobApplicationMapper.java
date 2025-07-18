package mate.academy.mapper;

import mate.academy.dto.JobApplicationDto;
import mate.academy.model.JobApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "job.title", target = "jobTitle")
    @Mapping(source = "job.company", target = "company")
    @Mapping(source = "status.status", target = "status")
    JobApplicationDto toDto(JobApplication application);
}
