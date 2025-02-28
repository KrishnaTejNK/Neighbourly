package com.dalhousie.Neighbourly.authentication.responseEntity;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetTokenResponse {
    private String token;
}
