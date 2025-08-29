package mate.academy.service;

public interface PasswordResetService {
    String generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
