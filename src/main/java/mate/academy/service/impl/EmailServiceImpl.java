package mate.academy.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    @Override
    public void sendResetLink(String toEmail, String token) {
        String subject = "Password Reset Request";
        String resetLink = resetPasswordUrl + "?token=" + token;
        String message = "Hello,\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "If you didn’t request this, you can ignore this email.\n\n"
                + "Best regards,\nYour App Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
