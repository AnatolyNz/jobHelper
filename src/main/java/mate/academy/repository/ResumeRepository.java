package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @Query("""
    SELECT DISTINCT r FROM Resume r
    LEFT JOIN FETCH r.jobMatches
    LEFT JOIN FETCH r.skills
    LEFT JOIN FETCH r.extractedSkills
    JOIN FETCH r.user u
    JOIN FETCH u.roles
    WHERE r.id = :id
            """)
    Optional<Resume> findByIdWithAllDetails(@Param("id") Long id);

    @Query("SELECT r FROM Resume r JOIN FETCH r.user WHERE r.id = :id")
    Optional<Resume> findWithUserById(@Param("id") Long id);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.skills WHERE r.id = :id")
    Optional<Resume> findByIdWithSkills(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT r
            FROM Resume r
            LEFT JOIN FETCH r.jobMatches
            LEFT JOIN FETCH r.skills
            LEFT JOIN FETCH r.extractedSkills
            JOIN FETCH r.user u
            JOIN FETCH u.roles
            WHERE r.id = :id
                    """)
    Optional<Resume> findByIdWithSkillsAndUserRoles(@Param("id") Long id);
}
