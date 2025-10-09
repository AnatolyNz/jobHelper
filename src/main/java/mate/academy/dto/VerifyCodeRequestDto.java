package mate.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeRequestDto(
        @NotBlank
        @Size(min = 4, max = 36, message = "Verification code must be between 4 and 36 characters")
        String token
) {
}
