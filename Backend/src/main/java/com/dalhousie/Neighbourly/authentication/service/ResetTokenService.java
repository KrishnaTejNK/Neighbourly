package com.dalhousie.Neighbourly.authentication.service;
import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import java.util.Optional;

public interface ResetTokenService {
    PasswordReset createResetPasswordToken(Integer userId);
    Optional<PasswordReset> findByUserId(Integer userId);

    void deleteResetPasswordToken(PasswordReset resetPasswordToken);
    boolean isTokenValid(PasswordReset token);

}
