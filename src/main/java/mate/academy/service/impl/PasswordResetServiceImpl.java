package mate.academy.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.model.PasswordResetToken;
import mate.academy.model.User;
import mate.academy.repository.PasswordResetTokenRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.EmailService;
import mate.academy.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found with email: " + email));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        try {
            System.out.println("BEFORE sending email");

            emailService.sendResetLink(email, token);

            System.out.println("AFTER sending email");
        } catch (Exception e) {
            System.out.println("EMAIL SENDING FAILED:");
            e.printStackTrace();

            throw new RuntimeException("Email sending failed", e);
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
