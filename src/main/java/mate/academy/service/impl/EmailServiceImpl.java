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

    @Override
    public void sendResetLink(String toEmail, String token) {
        String subject = "Password Reset Request";
        String resetLink = "код - " + token;
        String message = "Привіт,\n\n"
                + "Використайте код, щоб скинути пароль:\n"
                + resetLink + "\n\n"
                + "Якщо ви не надсилали цей запит, можете проігнорувати "
                + "цей електронний лист.\n\n"
                + "З найкращими побажаннями,\nВаша команда розробників";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
