package mate.academy.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.PasswordResetToken;
import mate.academy.model.User;
import mate.academy.repository.PasswordResetTokenRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.EmailService;
import mate.academy.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public void verifyPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid verification code"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code expired");
        }
    }

    @Override
    public String generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("Password reset requested for non-existing email: {}", email);
            return "dummy-token";
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        try {
            log.info("BEFORE sending email");

            emailService.sendResetLink(email, token);

            log.info("AFTER sending email");
        } catch (Exception e) {
            log.error("EMAIL SENDING FAILED", e);
        }

        return token;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
