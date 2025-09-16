package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import mate.academy.model.Job;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    @DisplayName("Save job")
    @Sql(scripts = "classpath:database/jobs/remove-jobs.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/jobs/remove-jobs.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void saveJob_ShouldReturnSavedJob() {
        Job job = new Job();
        job.setTitle("Backend Developer");
        job.setDescription("Work with Spring Boot");
        Job saved = jobRepository.save(job);

        assertNotNull(saved.getId());
        assertEquals("Backend Developer", saved.getTitle());
    }
}
