package mate.academy.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import mate.academy.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks private EmailServiceImpl emailService;

    @Test
    @DisplayName("Should send reset link email")
    void sendResetLink_ShouldSendEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendResetLink("to@example.com", "TOKEN123");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
