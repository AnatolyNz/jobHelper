package mate.academy.service;

public interface EmailService {
    void sendResetLink(String toEmail, String token);
}
