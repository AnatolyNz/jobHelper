package mate.academy.repository;

import java.util.Optional;
import mate.academy.model.AtsEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtsEvaluationRepository extends JpaRepository<AtsEvaluation, Long> {

    Optional<AtsEvaluation> findByResumeId(Long resumeId);
}
