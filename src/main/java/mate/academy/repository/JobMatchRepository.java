package mate.academy.repository;

import java.util.List;
import mate.academy.model.Job;
import mate.academy.model.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.requiredSkills")
    List<Job> findAllWithRequiredSkills();
}
