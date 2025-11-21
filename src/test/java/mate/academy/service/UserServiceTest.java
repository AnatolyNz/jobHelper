package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.mapper.UserMapper;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.RoleRepository;
import mate.academy.repository.UserRepository;
import mate.academy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserServiceImpl userService;

    private UserRegistrationRequestDto request;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        request = new UserRegistrationRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");

        user = new User();
        user.setEmail(request.getEmail());

        role = new Role();
        role.setRoleName(Role.RoleName.USER);
    }

    @Test
    @DisplayName("Should register new user when email is not taken")
    void register_ValidRequest_ShouldReturnUserResponseDto() throws RegistrationException {
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(request.getEmail());

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(Role.RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDto actual = userService.register(request);

        assertEquals(expectedResponse, actual);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RegistrationException when email already exists")
    void register_ExistingEmail_ShouldThrowException() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(RegistrationException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when USER role missing")
    void register_NoUserRoleFound_ShouldThrowException() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(Role.RoleName.USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.register(request));
    }

    @Test
    @DisplayName("Should return UserResponseDto when user found by email")
    void getByEmail_ExistingUser_ShouldReturnUserResponseDto() {
        User userWithRoles = new User();
        userWithRoles.setEmail(request.getEmail());
        userWithRoles.setRoles(Set.of(role));

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setEmail(request.getEmail());

        when(userRepository.findByEmailWithRoles(request.getEmail()))
                .thenReturn(Optional.of(userWithRoles));
        when(userMapper.toResponseDto(userWithRoles))
                .thenReturn(expectedResponse);

        UserResponseDto actual = userService.getByEmail(request.getEmail());

        assertEquals(expectedResponse, actual);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user not found by email")
    void getByEmail_NonExistingUser_ShouldThrowException() {
        when(userRepository.findByEmailWithRoles(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService
                .getByEmail(request.getEmail()));
    }
}
