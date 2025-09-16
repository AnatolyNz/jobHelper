package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Find role by RoleName")
    @Sql(scripts = "classpath:database/roles/add-roles.sql", executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/roles/remove-roles.sql", executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByRoleName_ShouldReturnRole() {
        Optional<Role> admin = roleRepository.findByRoleName(Role.RoleName.ADMIN);

        assertTrue(admin.isPresent());
        assertEquals(Role.RoleName.ADMIN, admin.get().getRoleName());
    }
}
