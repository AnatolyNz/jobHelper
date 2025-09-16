package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.model.PasswordResetToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Test
    @DisplayName("Find token with user by token value")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/tokens/add-password-reset-tokens.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tokens/remove-password-reset-tokens.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByTokenWithUser_ShouldReturnTokenWithUser() {
        Optional<PasswordResetToken> token = tokenRepository.findByTokenWithUser("token123");

        assertTrue(token.isPresent());
        assertEquals("token123", token.get().getToken());
        assertTrue(token.get().getUser() != null);
    }

    @Test
    @DisplayName("Delete token by value")
    @Sql(scripts = {
            "classpath:database/roles/add-roles.sql",
            "classpath:database/users/add-users.sql",
            "classpath:database/tokens/add-password-reset-tokens.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/tokens/remove-password-reset-tokens.sql",
            "classpath:database/users/remove-users.sql",
            "classpath:database/roles/remove-roles.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteByToken_ShouldRemoveToken() {
        tokenRepository.deleteByToken("token123");
        Optional<PasswordResetToken> token = tokenRepository.findByTokenWithUser("token123");
        assertFalse(token.isPresent());
    }
}
