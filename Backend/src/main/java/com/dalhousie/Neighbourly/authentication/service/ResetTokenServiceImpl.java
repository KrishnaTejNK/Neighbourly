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
        deleteExistingTokenIfPresent(userId);

        PasswordReset passwordReset = generatePasswordResetToken(userId);
        log.info("Generated token: {}", passwordReset);
        return passwordResetTokenRepository.save(passwordReset);
    }

    private void deleteExistingTokenIfPresent(Integer userId) {
        passwordResetTokenRepository.findByUserId(userId)
                .ifPresent(passwordResetTokenRepository::delete);
    }

    private PasswordReset generatePasswordResetToken(Integer userId) {
        return PasswordReset.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(EXPIRATION_DURATION))
                .build();
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
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(PasswordReset token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token has expired.");
        }
        return false;
    }
}
