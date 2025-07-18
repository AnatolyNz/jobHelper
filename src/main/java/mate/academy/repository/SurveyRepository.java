package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findByUserId(Long userId);
}
