package mate.academy.repository;

import java.util.List;
import mate.academy.model.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
    List<JobMatch> findAllByResumeId(Long resumeId);
}
