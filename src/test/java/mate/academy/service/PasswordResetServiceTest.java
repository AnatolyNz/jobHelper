package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.PasswordResetToken;
import mate.academy.model.User;
import mate.academy.repository.PasswordResetTokenRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordResetTokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks private PasswordResetServiceImpl passwordResetService;

    @Test
    @DisplayName("Should generate token and send email")
    void generatePasswordResetToken_ShouldReturnToken() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String token = passwordResetService.generatePasswordResetToken(user.getEmail());

        assertNotNull(token);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendResetLink(eq(user.getEmail()), anyString());
    }

    @Test
    @DisplayName("Should throw exception if user not found")
    void generatePasswordResetToken_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> passwordResetService.generatePasswordResetToken("missing@example.com"));
    }

    @Test
    @DisplayName("Should reset password for valid token")
    void resetPassword_ValidToken_ShouldUpdatePassword() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        User user = new User();
        token.setUser(user);

        when(tokenRepository.findByTokenWithUser(token.getToken())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        passwordResetService.resetPassword(token.getToken(), "newPass");

        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
        assertEquals("encodedPass", user.getPassword());
    }

    @Test
    @DisplayName("Should throw exception for expired token")
    void resetPassword_ExpiredToken_ShouldThrowException() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        token.setUser(new User());

        when(tokenRepository.findByTokenWithUser(token.getToken())).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(token.getToken(), "newPass"));
    }

    @Test
    @DisplayName("Should throw exception for invalid token")
    void resetPassword_InvalidToken_ShouldThrowException() {
        when(tokenRepository.findByTokenWithUser("invalid")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> passwordResetService.resetPassword("invalid", "newPass"));
    }

    @Test
    @DisplayName("Should verify valid token without exception")
    void verifyPasswordResetToken_ValidToken_ShouldPass() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("valid");
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        token.setUser(new User());

        when(tokenRepository.findByTokenWithUser("valid")).thenReturn(Optional.of(token));

        passwordResetService.verifyPasswordResetToken("valid");
    }

    @Test
    @DisplayName("Should throw exception for expired token verification")
    void verifyPasswordResetToken_ExpiredToken_ShouldThrowException() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        token.setUser(new User());

        when(tokenRepository.findByTokenWithUser("expired")).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.verifyPasswordResetToken("expired"));
    }

    @Test
    @DisplayName("Should throw exception for invalid token verification")
    void verifyPasswordResetToken_InvalidToken_ShouldThrowException() {
        when(tokenRepository.findByTokenWithUser("invalid")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> passwordResetService.verifyPasswordResetToken("invalid"));
    }
}
