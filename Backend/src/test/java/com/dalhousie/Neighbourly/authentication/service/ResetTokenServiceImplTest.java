package com.dalhousie.Neighbourly.authentication.service;

import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import com.dalhousie.Neighbourly.authentication.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResetTokenServiceImplTest {

    private static final int TEST_USER_ID = 1;
    private static final long TOKEN_EXPIRY_DURATION_MS = 10 * 60 * 1000L; // 10 minutes
    private static final long TOKEN_ALREADY_EXPIRED_MS = 1000L; // 1 second
    private static final String EXPIRED_TOKEN_MESSAGE = "Token has expired.";
    private static final int EXPECTED_CALL_COUNT = 1;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private ResetTokenServiceImpl resetTokenService;

    private PasswordReset passwordReset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        passwordReset = PasswordReset.builder()
                .userId(TEST_USER_ID)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(TOKEN_EXPIRY_DURATION_MS))
                .build();
    }

    @Test
    void testCreateResetPasswordToken() {
        when(passwordResetTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(passwordReset);

        PasswordReset createdToken = resetTokenService.createResetPasswordToken(TEST_USER_ID);

        assertNotNull(createdToken);
        assertEquals(TEST_USER_ID, createdToken.getUserId());
        assertNotNull(createdToken.getToken());
        assertTrue(createdToken.getExpiryDate().isAfter(Instant.now()));

        verify(passwordResetTokenRepository, times(EXPECTED_CALL_COUNT)).save(any(PasswordReset.class));
    }

    @Test
    void testCreateResetPasswordToken_ExistingTokenDeleted() {
        when(passwordResetTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(passwordReset));
        when(passwordResetTokenRepository.save(any(PasswordReset.class))).thenReturn(passwordReset);

        PasswordReset createdToken = resetTokenService.createResetPasswordToken(TEST_USER_ID);

        verify(passwordResetTokenRepository, times(EXPECTED_CALL_COUNT)).delete(any(PasswordReset.class));
        verify(passwordResetTokenRepository, times(EXPECTED_CALL_COUNT)).save(any(PasswordReset.class));
    }

    @Test
    void testFindByUserId() {
        when(passwordResetTokenRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(passwordReset));

        Optional<PasswordReset> foundToken = resetTokenService.findByUserId(TEST_USER_ID);

        assertTrue(foundToken.isPresent());
        assertEquals(TEST_USER_ID, foundToken.get().getUserId());
    }

    @Test
    void testDeleteResetPasswordToken() {
        doNothing().when(passwordResetTokenRepository).delete(passwordReset);

        resetTokenService.deleteResetPasswordToken(passwordReset);

        verify(passwordResetTokenRepository, times(EXPECTED_CALL_COUNT)).delete(passwordReset);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        boolean isValid = resetTokenService.isTokenValid(passwordReset);
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        PasswordReset expiredToken = PasswordReset.builder()
                .userId(TEST_USER_ID)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().minusMillis(TOKEN_ALREADY_EXPIRED_MS))
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            resetTokenService.isTokenValid(expiredToken);
        });

        assertEquals(EXPIRED_TOKEN_MESSAGE, exception.getMessage());
    }

    @Test
    void testIsTokenValid_NullToken() {
        boolean isValid = resetTokenService.isTokenValid(null);
        assertFalse(isValid);
    }
}