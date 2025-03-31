package com.dalhousie.Neighbourly.authentication.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.dalhousie.Neighbourly.authentication.entity.Otp;
import com.dalhousie.Neighbourly.authentication.repository.OtpRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;

    @Transactional
    @Override
    public Otp generateOtp(Integer userId) {
        int hours = 60;
        int sec = 10;
        long duration = 1000L * hours * sec;
        long ransomStart = 100000;
        int bound = 900000;
        Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
        existingOtp.ifPresent(this::deleteOtp);

        String otpValue = String.valueOf(ransomStart + new Random().nextInt(bound)) ;

        Otp otp = Otp.builder()
                .otp(otpValue)
                .expiryDate(Instant.now().plusMillis(duration))
                .userId(userId)
                .build();

        return otpRepository.save(otp);
    }
    public Otp resendOtp(Integer userId) {
        Optional<Otp> existingOtp = otpRepository.findByUserId(userId);
        existingOtp.ifPresent(this::deleteOtp);
        return generateOtp(userId);
    }

    @Transactional
    @Override
    public void deleteOtp(Otp otp) {
        if (otp != null) {
            otpRepository.deleteByUserId(otp.getUserId());
        }
    }


    @Override
    public Optional<Otp> findByOtp(String otpValue) {
        return otpRepository.findByOtp(otpValue);
    }

    public boolean isOtpValid(Otp otp) {
        if (otp == null || otp.getExpiryDate() == null) {
            return false;
        }
        if (otp.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException("otp has expired.");
        }

        return true;
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }
    
}


