package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.ApiResponse;
import mate.academy.dto.ForgotPasswordRequestDto;
import mate.academy.dto.ResetPasswordRequestDto;
import mate.academy.dto.UserLoginRequestDto;
import mate.academy.dto.UserLoginResponseDto;
import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.dto.VerifyCodeRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.exception.RegistrationException;
import mate.academy.security.AuthenticationService;
import mate.academy.service.PasswordResetService;
import mate.academy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Logs in an existing user")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends reset link to user email")
    public ResponseEntity<String> forgotPassword(@RequestBody
                                                     @Valid ForgotPasswordRequestDto request) {
        passwordResetService.generatePasswordResetToken(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to email if account exists.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password",
            description = "Resets user password using provided token")
    public ResponseEntity<String> resetPassword(@RequestBody
                                                    @Valid ResetPasswordRequestDto request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }

    @PostMapping("/verify-code")
    @Operation(summary = "Verify password reset code", description =
            "Checks if the provided password reset code is valid")
    public ResponseEntity<ApiResponse> verifyCode(@RequestBody @Valid
                                                      VerifyCodeRequestDto request) {
        try {
            passwordResetService.verifyPasswordResetToken(request.token());
            return ResponseEntity.ok(new ApiResponse("Code verified"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Invalid verification code"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Verification code expired"));
        }
    }
}
