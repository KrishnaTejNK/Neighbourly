package com.dalhousie.Neighbourly.authentication.service;

import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import com.dalhousie.Neighbourly.authentication.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetTokenServiceImpl implements ResetTokenService{
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final long EXPIRATION_DURATION = 1000L * 60 * 10;
    @Override
    public PasswordReset createResetPasswordToken(Integer userId) {

        passwordResetTokenRepository.findByUserId(userId).ifPresent(passwordResetTokenRepository::delete);

        // Generating new random token
        PasswordReset passwordReset = PasswordReset.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString()) 
                .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION))
                .build();
        log.info(String.valueOf(passwordReset));
        return passwordResetTokenRepository.save(passwordReset);
    }
    @Override
    public Optional<PasswordReset> findByUserId(Integer userId) {
        return passwordResetTokenRepository.findByUserId(userId);
    }
    @Override
    public void deleteResetPasswordToken(PasswordReset resetPasswordToken) {
        if (resetPasswordToken != null) {
            passwordResetTokenRepository.delete(resetPasswordToken);
        }
    }
    @Override
    public boolean isTokenValid(PasswordReset token) {
        if (token == null || token.getExpiryDate() == null) {
            return false;
        }
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("token has expired.");
        }

        return true;
    }
}
