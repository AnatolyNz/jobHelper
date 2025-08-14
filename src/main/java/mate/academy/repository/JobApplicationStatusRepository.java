package mate.academy.repository;

import mate.academy.model.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationStatusRepository extends JpaRepository<JobApplicationStatus, Long> {}
