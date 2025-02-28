package com.dalhousie.Neighbourly.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dalhousie.Neighbourly.authentication.entity.Otp;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByOtp(String otp);
    Optional<Otp> findByUserId(Integer userId); 
    void deleteByUserId(Integer userId);
}
