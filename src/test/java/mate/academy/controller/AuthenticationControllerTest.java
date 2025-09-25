package mate.academy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.ForgotPasswordRequestDto;
import mate.academy.dto.ResetPasswordRequestDto;
import mate.academy.dto.UserLoginRequestDto;
import mate.academy.dto.UserLoginResponseDto;
import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.exception.RegistrationException;
import mate.academy.security.AuthenticationService;
import mate.academy.service.PasswordResetService;
import mate.academy.service.UserService;
import mate.academy.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private EmailServiceImpl emailServiceImpl;

    @Test
    @DisplayName("Register user - should return CREATED status")
    void register_ShouldReturnCreated() throws Exception {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("user@example.com")
                .setPassword("password123")
                .setRepeatPassword("password123");

        UserResponseDto response = new UserResponseDto()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("user@example.com");

        when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Register user - should return 400 if registration fails")
    void register_ShouldReturnBadRequest_WhenRegistrationFails() throws Exception {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("user@example.com")
                .setPassword("password123")
                .setRepeatPassword("password123");

        Mockito.doThrow(new RegistrationException("Email already exists"))
                .when(userService).register(any(UserRegistrationRequestDto.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - should return JWT token response")
    void login_ShouldReturnToken() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto(
                "user@example.com",
                "password123"
        );

        UserLoginResponseDto response = new UserLoginResponseDto("jwt-token-123");

        when(authenticationService.authenticate(any(UserLoginRequestDto
                .class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void forgotPassword_ShouldReturnOk() throws Exception {
        ForgotPasswordRequestDto request =
                new ForgotPasswordRequestDto("user@example.com");

        when(passwordResetService.generatePasswordResetToken("user@example.com"))
                .thenReturn("dummy-token");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Password reset link sent to email if account exists."));
    }

    @Test
    @DisplayName("Reset password - should return success message")
    void resetPassword_ShouldReturnOk() throws Exception {
        ResetPasswordRequestDto request = new ResetPasswordRequestDto("token123", "newPassword123");

        doNothing().when(passwordResetService).resetPassword("token123", "newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully reset."));
    }
}
