package mate.academy.repository;

import java.util.List;
import mate.academy.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("""
        SELECT DISTINCT ja
        FROM JobApplication ja
        JOIN FETCH ja.job j
        LEFT JOIN FETCH j.requiredSkills
        JOIN FETCH ja.status s
        WHERE ja.user.id = :userId
            """)
    List<JobApplication> findByUserIdWithDetails(@Param("userId") Long userId);

    @Query("""
        SELECT ja
        FROM JobApplication ja
        JOIN FETCH ja.job j
        LEFT JOIN FETCH j.requiredSkills
        JOIN FETCH ja.status s
        WHERE ja.id = :id
            """)
    JobApplication findByIdWithDetails(@Param("id") Long id);
}
