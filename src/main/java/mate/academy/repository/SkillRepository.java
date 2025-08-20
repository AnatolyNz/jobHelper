package mate.academy.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT s.name FROM Job j JOIN j.requiredSkills s")
    List<String> findAllSkillsUsedInJobs();
}
