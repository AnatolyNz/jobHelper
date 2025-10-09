package mate.academy.service;

public interface PasswordResetService {
    void verifyPasswordResetToken(String token);

    String generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
