package mate.academy.repository;

import mate.academy.model.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
}
