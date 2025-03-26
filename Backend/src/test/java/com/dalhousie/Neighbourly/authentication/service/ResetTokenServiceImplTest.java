package com.dalhousie.Neighbourly.authentication.service;

import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import com.dalhousie.Neighbourly.authentication.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetTokenServiceImplTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private ResetTokenServiceImpl resetTokenService;

    private PasswordReset passwordReset;
    private final Integer userId = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setting up a PasswordReset entity for testing
        passwordReset = PasswordReset.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 10)) // token valid for 10 minutes
                .build();
    }

    @Test
    void testCreateResetPasswordToken() {
        // Mock the behavior of passwordResetTokenRepository.findByUserId to return empty
        Mockito.when(passwordResetTokenRepository.findByUserId(userId)).thenReturn(Optional.empty());
        // Mock the behavior of passwordResetTokenRepository.save to return the passwordReset entity
        Mockito.when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(passwordReset);

        PasswordReset createdToken = resetTokenService.createResetPasswordToken(userId);

        // Assert the token is created and saved
        assertNotNull(createdToken);
        assertEquals(userId, createdToken.getUserId());
        assertNotNull(createdToken.getToken());
        assertTrue(createdToken.getExpiryDate().isAfter(Instant.now()));

        // Verify that save method was called once
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordReset.class));
    }

    @Test
    void testCreateResetPasswordToken_ExistingTokenDeleted() {
        // Mock the behavior of passwordResetTokenRepository.findByUserId to return an existing token
        Mockito.when(passwordResetTokenRepository.findByUserId(userId)).thenReturn(Optional.of(passwordReset));

        // Mock the behavior of passwordResetTokenRepository.save to return the new password reset token
        Mockito.when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(passwordReset);

        // Create a new reset token (which should delete the existing one)
        PasswordReset createdToken = resetTokenService.createResetPasswordToken(userId);

        // Verify that delete method was called once
        verify(passwordResetTokenRepository, times(1)).delete(any(PasswordReset.class));
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordReset.class));
    }

    @Test
    void testFindByUserId() {
        // Mock the behavior of passwordResetTokenRepository.findByUserId to return an existing token
        Mockito.when(passwordResetTokenRepository.findByUserId(userId)).thenReturn(Optional.of(passwordReset));

        Optional<PasswordReset> foundToken = resetTokenService.findByUserId(userId);

        // Assert that the token is found and matches the userId
        assertTrue(foundToken.isPresent());
        assertEquals(userId, foundToken.get().getUserId());
    }

    @Test
    void testDeleteResetPasswordToken() {
        // Mock behavior for deletion, assuming it's successful
        doNothing().when(passwordResetTokenRepository).delete(passwordReset);

        // Delete the password reset token
        resetTokenService.deleteResetPasswordToken(passwordReset);

        // Verify delete method was called once
        verify(passwordResetTokenRepository, times(1)).delete(passwordReset);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        boolean isValid = resetTokenService.isTokenValid(passwordReset);

        // Assert that the token is valid as it has not expired
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        // Create an expired token
        PasswordReset expiredToken = PasswordReset.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().minusMillis(1000L)) // expired token
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            resetTokenService.isTokenValid(expiredToken);
        });

        // Assert that the exception is thrown with the correct message
        assertEquals("token has expired.", exception.getMessage());
    }

    @Test
    void testIsTokenValid_NullToken() {
        boolean isValid = resetTokenService.isTokenValid(null);

        // Assert that the token is not valid because it is null
        assertFalse(isValid);
    }
}
