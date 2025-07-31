package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUserId(Long userId);

    @Query("SELECT r FROM Resume r JOIN FETCH r.user WHERE r.id = :id")
    Optional<Resume> findWithUserById(@Param("id") Long id);
}
