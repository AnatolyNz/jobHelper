package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
    SELECT DISTINCT j
    FROM Job j
    LEFT JOIN FETCH j.requiredSkills
            """)
    Page<Job> findAllWithRequiredSkills(Pageable pageable);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.requiredSkills WHERE j.id = :jobId")
    Optional<Job> findByIdWithRequiredSkills(Long jobId);
}
