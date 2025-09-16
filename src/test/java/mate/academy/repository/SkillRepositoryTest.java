package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Skill;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SkillRepositoryTest {

    @Autowired
    private SkillRepository skillRepository;

    @Test
    @DisplayName("Find skill by name ignoring case")
    @Sql(scripts = "classpath:database/skills/add-skills.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/skills/remove-skills.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByNameIgnoreCase_ShouldReturnSkill() {
        Optional<Skill> skill = skillRepository.findByNameIgnoreCase("java");

        assertTrue(skill.isPresent());
        assertEquals("Java", skill.get().getName());
    }

    @Test
    @DisplayName("Find all skills used in jobs")
    @Sql(scripts = {
            "classpath:database/jobs/add-jobs.sql",
            "classpath:database/skills/add-skills.sql",
            "classpath:database/skills/add-skills-with-jobs.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/skills/remove-skills.sql",
            "classpath:database/jobs/remove-jobs.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllSkillsUsedInJobs_ShouldReturnList() {
        List<String> skills = skillRepository.findAllSkillsUsedInJobs();

        assertFalse(skills.isEmpty());
        assertTrue(skills.contains("Java"));
    }
}
