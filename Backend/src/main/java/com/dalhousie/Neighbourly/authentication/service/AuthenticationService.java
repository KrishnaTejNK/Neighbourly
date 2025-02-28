
package com.dalhousie.Neighbourly.authentication.service;
import com.dalhousie.Neighbourly.authentication.requestEntity.RegisterRequest;
import com.dalhousie.Neighbourly.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.Neighbourly.authentication.responseEntity.PasswordResetTokenResponse;

import jakarta.servlet.http.HttpServletRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.OtpVerificationRequest;
public interface AuthenticationService {
    AuthenticationResponse registerUser(RegisterRequest registerRequest);  
    void resendOtp(String email);
    public String getURL(HttpServletRequest request);
    AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest);
    AuthenticationResponse verifyOtp(OtpVerificationRequest otpVerificationRequest);
    PasswordResetTokenResponse forgotPassword(String email, String resetUrl);
    void resetPassword(String email, String password, String token);
}