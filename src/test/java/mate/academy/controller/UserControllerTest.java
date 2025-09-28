package mate.academy.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mate.academy.dto.UserResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private Authentication authentication;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        authentication = Mockito.mock(Authentication.class);
        objectMapper = new ObjectMapper();

        // Створюємо контролер вручну
        UserController userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new mate.academy.exception
                        .CustomGlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /users/me - should return current user")
    void getCurrentUser_ShouldReturnUserResponseDto() throws Exception {
        UserResponseDto response = new UserResponseDto()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("user@example.com");

        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getByEmail(anyString())).thenReturn(response);

        mockMvc.perform(get("/users/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("GET /users/me - should return 404 if user not found")
    void getCurrentUser_ShouldReturnNotFound_WhenUserMissing() throws Exception {
        when(authentication.getName()).thenReturn("notfound@example.com");
        when(userService.getByEmail(anyString()))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/users/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
