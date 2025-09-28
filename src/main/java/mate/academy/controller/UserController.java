package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.UserResponseDto;
import mate.academy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Returns the profile information of the currently logged-in user"
    )
    public UserResponseDto getCurrentUser(Authentication authentication) {
        return userService.getByEmail(authentication.getName());
    }
}
