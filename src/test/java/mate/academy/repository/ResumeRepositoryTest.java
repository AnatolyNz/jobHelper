package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.model.Resume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ResumeRepositoryTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Test
    @DisplayName("Find resume with all details by ID")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/users/add-users-roles.sql",
            "classpath:database/skills/add-skills.sql",
            "classpath:database/jobs/add-jobs.sql",
            "classpath:database/skills/add-skills-with-jobs.sql",
            "classpath:database/resumes/add-resumes.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/resumes/remove-resumes.sql",
            "classpath:database/skills/remove-skills-with-jobs.sql",
            "classpath:database/jobs/remove-jobs.sql",
            "classpath:database/skills/remove-skills.sql",
            "classpath:database/users/remove-users-roles.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithAllDetails_ShouldReturnResume() {
        Optional<Resume> resume = resumeRepository.findByIdWithAllDetails(1L);
        assertTrue(resume.isPresent());
        assertNotNull(resume.get().getSkills());
        assertNotNull(resume.get().getUser());
        assertFalse(resume.get().getUser().getRoles().isEmpty());
    }
}
