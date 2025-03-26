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

    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    private Otp otp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otp = Otp.builder()
                .otp("123456")
                .expiryDate(Instant.now().plusMillis(1000L * 60 * 10))
                .userId(1)
                .build();
    }

    @Test
    void testGenerateOtp() {
        // Mock the behavior of otpRepository.save
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp generatedOtp = otpService.generateOtp(1);

        assertNotNull(generatedOtp);
        assertEquals(otp.getOtp(), generatedOtp.getOtp());
        assertEquals(otp.getUserId(), generatedOtp.getUserId());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }

    @Test
    void testResendOtp() {
        // Mock the behavior of otpRepository.findByUserId
        Mockito.when(otpRepository.findByUserId(anyInt())).thenReturn(Optional.of(otp));
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp newOtp = otpService.resendOtp(1);

        assertNotNull(newOtp);
        assertEquals(otp.getOtp(), newOtp.getOtp());

        // Verify that deleteByUserId is called once, despite being called indirectly
        Mockito.verify(otpRepository, Mockito.times(2)).deleteByUserId(Mockito.anyInt());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }


    @Test
    void testDeleteOtp() {
        // Mock the delete behavior
        Mockito.doNothing().when(otpRepository).deleteByUserId(anyInt());

        otpService.deleteOtp(otp);

        Mockito.verify(otpRepository).deleteByUserId(anyInt());
    }

    @Test
    void testFindByOtp() {
        // Mock the behavior of otpRepository.findByOtp
        Mockito.when(otpRepository.findByOtp(anyString())).thenReturn(Optional.of(otp));

        Optional<Otp> foundOtp = otpService.findByOtp("123456");

        assertTrue(foundOtp.isPresent());
        assertEquals(otp.getOtp(), foundOtp.get().getOtp());
    }

    @Test
    void testIsOtpValid_ValidOtp() {
        // Check for valid OTP
        boolean isValid = otpService.isOtpValid(otp);

        assertTrue(isValid);
    }

    @Test
    void testIsOtpValid_InvalidOtp() {
        // Set OTP expiry to a past time
        otp.setExpiryDate(Instant.now().minusMillis(1000L * 60 * 10));

        // Check for expired OTP
        assertThrows(OtpServiceImpl.TokenExpiredException.class, () -> otpService.isOtpValid(otp));
    }

    @Test
    void testGenerateOtp_WhenExistingOtpExists() {
        // Mock the behavior of otpRepository.findByUserId
        Mockito.when(otpRepository.findByUserId(anyInt())).thenReturn(Optional.of(otp));
        Mockito.when(otpRepository.save(Mockito.any(Otp.class))).thenReturn(otp);

        Otp newOtp = otpService.generateOtp(1);

        assertNotNull(newOtp);
        assertEquals(otp.getOtp(), newOtp.getOtp());
        Mockito.verify(otpRepository).deleteByUserId(Mockito.anyInt());
        Mockito.verify(otpRepository).save(Mockito.any(Otp.class));
    }

}
