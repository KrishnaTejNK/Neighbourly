package com.dalhousie.Neighbourly.authentication.service;

import com.dalhousie.Neighbourly.authentication.entity.Otp;
import com.dalhousie.Neighbourly.authentication.repository.OtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class OtpServiceImplTest {

    private static final String TEST_OTP = "123456";
    private static final int TEST_USER_ID = 1;
    private static final long OTP_EXPIRY_DURATION_MS = 1000L * 60 * 10; // 10 minutes
    private static final int EXPECTED_DELETE_CALL_COUNT = 2;

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    private Otp otp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otp = Otp.builder()
                .otp(TEST_OTP)
                .expiryDate(Instant.now().plusMillis(OTP_EXPIRY_DURATION_MS))
                .userId(TEST_USER_ID)
                .build();
    }

    @Test
    void testGenerateOtp() {
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp generatedOtp = otpService.generateOtp(TEST_USER_ID);

        assertNotNull(generatedOtp);
        assertEquals(TEST_OTP, generatedOtp.getOtp());
        assertEquals(TEST_USER_ID, generatedOtp.getUserId());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }

    @Test
    void testResendOtp() {
        Mockito.when(otpRepository.findByUserId(anyInt())).thenReturn(Optional.of(otp));
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp newOtp = otpService.resendOtp(TEST_USER_ID);

        assertNotNull(newOtp);
        assertEquals(TEST_OTP, newOtp.getOtp());
        Mockito.verify(otpRepository, Mockito.times(EXPECTED_DELETE_CALL_COUNT)).deleteByUserId(anyInt());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }

    @Test
    void testDeleteOtp() {
        Mockito.doNothing().when(otpRepository).deleteByUserId(anyInt());
        otpService.deleteOtp(otp);
        Mockito.verify(otpRepository).deleteByUserId(anyInt());
    }

    @Test
    void testFindByOtp() {
        Mockito.when(otpRepository.findByOtp(anyString())).thenReturn(Optional.of(otp));

        Optional<Otp> foundOtp = otpService.findByOtp(TEST_OTP);

        assertTrue(foundOtp.isPresent());
        assertEquals(TEST_OTP, foundOtp.get().getOtp());
    }

    @Test
    void testIsOtpValid_ValidOtp() {
        assertTrue(otpService.isOtpValid(otp));
    }

    @Test
    void testIsOtpValid_InvalidOtp() {
        otp.setExpiryDate(Instant.now().minusMillis(OTP_EXPIRY_DURATION_MS));
        assertThrows(OtpServiceImpl.TokenExpiredException.class, () -> otpService.isOtpValid(otp));
    }

    @Test
    void testGenerateOtp_WhenExistingOtpExists() {
        Mockito.when(otpRepository.findByUserId(anyInt())).thenReturn(Optional.of(otp));
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp newOtp = otpService.generateOtp(TEST_USER_ID);

        assertNotNull(newOtp);
        assertEquals(TEST_OTP, newOtp.getOtp());
        Mockito.verify(otpRepository).deleteByUserId(anyInt());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }
}