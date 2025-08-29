package mate.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank String token,
        @NotBlank @Size(min = 7, max = 100) String newPassword
) {
    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
