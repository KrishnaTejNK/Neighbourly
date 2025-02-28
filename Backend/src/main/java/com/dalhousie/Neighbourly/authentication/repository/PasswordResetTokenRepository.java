package com.dalhousie.Neighbourly.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordReset,Integer> {
    Optional<PasswordReset> findByUserId(Integer userId);
}
