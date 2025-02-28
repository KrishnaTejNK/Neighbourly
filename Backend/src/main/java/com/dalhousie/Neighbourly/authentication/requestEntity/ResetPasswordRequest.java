package com.dalhousie.Neighbourly.authentication.requestEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    // $2a$10$oWH67PsqlgLbHzEWw72/tOLyEL4ce4ksP3shSOqnP1dysZt2X.9eO
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must have at least 8 characters!")
    private String password;

    @NotBlank(message = "Token is required")
    private String token;
}
