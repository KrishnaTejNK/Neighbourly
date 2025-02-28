package com.dalhousie.Neighbourly.authentication.service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

import com.dalhousie.Neighbourly.user.entity.UserType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dalhousie.Neighbourly.authentication.entity.Otp;
import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import com.dalhousie.Neighbourly.authentication.jwt.JwtService;
import com.dalhousie.Neighbourly.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.OtpVerificationRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.RegisterRequest;
import com.dalhousie.Neighbourly.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.Neighbourly.authentication.responseEntity.PasswordResetTokenResponse;
import com.dalhousie.Neighbourly.authentication.service.OtpServiceImpl.TokenExpiredException;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.MessagingException;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final ResetTokenService resetTokenService;

    @Transactional
    @Override
    public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
        if (userService.isUserPresent(registerRequest.getEmail())) {
            throw new RuntimeException("provided user already exists");
        }
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userType(UserType.USER)
                .build();
        userService.saveUser(user);
        log.info("user entered");
        Otp otp = otpService.generateOtp(user.getId());
        prepareAndDispatchOtpMail(otp.getOtp(), user.getEmail());
        return AuthenticationResponse.builder()
                .token(null)
                .build();
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest) {

        if (!userService.isUserPresent(authenticateRequest.getEmail())) {
            throw new UsernameNotFoundException("User not found with email: " + authenticateRequest.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateRequest.getEmail(),
                        authenticateRequest.getPassword()));
        var user = userService.findUserByEmail(authenticateRequest.getEmail()).get();
        if (!userService.isUserPresent(authenticateRequest.getEmail())) {
            throw new UsernameNotFoundException("User not found with email: " + authenticateRequest.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateRequest.getEmail(),
                        authenticateRequest.getPassword()));
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify before logging in.");
        }

        var jwtToken = jwtService.generateToken(user, user.isEmailVerified());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userType(user.getUserType().name())  // Convert ENUM to String
                .neighbourhoodId(user.getNeighbourhood_id())  // Get neighbourhood ID
                .build();
    }

    @Override
    @Transactional
    public void resendOtp(String email) {
        Optional<User> user = userService.findUserByEmail(email);
        Otp otp = otpService.resendOtp(user.get().getId());
        if (!user.isPresent()) {
            throw new RuntimeException("User not found with ID: " + user.get().getId());
        }
        prepareAndDispatchOtpMail(otp.getOtp(), user.get().getEmail());
    }



    @Override
    public AuthenticationResponse verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        Optional<Otp> otpOptional = otpService.findByOtp(otpVerificationRequest.getOtp());
        if (otpOptional.isEmpty()) {
            throw new TokenExpiredException("Invalid OTP. Please try again.");
        }
        Otp otp = otpOptional.get();
        if (!otpService.isOtpValid(otp)) {
            throw new TokenExpiredException("OTP has expired. Please request a new one.");
        }

        User user = userService.findUserById(otp.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + otp.getUserId()));
        user.setEmailVerified(true);
        //userService.saveUser(user);

        String jwtToken = jwtService.generateToken(user, user.isEmailVerified());

        otpService.deleteOtp(otp);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private void prepareAndDispatchOtpMail(String otp, String mail) {
        String subject = "Verify Your Email";
        String content = "<p>Hello,</p>"
                + "<p>Your OTP for email verification is:</p>"
                + "<h2>" + otp + "</h2>"
                + "<p>This OTP is valid for 5 minutes.</p>";
        dispatchEmail(subject, content, mail);
    }

    private void dispatchEmail(String mailSubject, String mailBody, String recipientEmail) {
        try {
            MimeMessage emailMessage = mailSender.createMimeMessage();
            MimeMessageHelper emailHelper = new MimeMessageHelper(emailMessage, true);

            emailHelper.setFrom("noreply@example.com", "Support Team");
            emailHelper.setTo(recipientEmail);
            emailHelper.setSubject(mailSubject);
            emailHelper.setText(mailBody, true);

            mailSender.send(emailMessage);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new RuntimeException("Error sending email: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void resetPassword(String email, String password, String token) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if(!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify before resetting password.");
        }
        PasswordReset passwordReset = resetTokenService.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("User did not initiate a reset password request."));

        if (!Objects.equals(token, passwordReset.getToken())) {
            throw new RuntimeException("Failed to authenticate token. Please request to reset your password again.");
        }

        if (!resetTokenService.isTokenValid(passwordReset)) {
            throw new RuntimeException("Token expired. Please request to reset your password again.");
        }

        String newPassword = passwordEncoder.encode(password);
        userService.updatePassword(email, newPassword);

        resetTokenService.deleteResetPasswordToken(passwordReset);
    }

    @Override
    public PasswordResetTokenResponse forgotPassword(String email, String resetUrl) {
       String resetToken = "";
        try {
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            resetToken = resetTokenService.createResetPasswordToken(user.getId()).getToken();
            log.info("Generated token: {}", resetToken);

            String resetPasswordLink = resetUrl + "?email=" + email;
            log.info("Reset password link: {}", resetPasswordLink);

            prepareAndDispatchResetPwdLink(resetPasswordLink,email);
        } catch (Exception e) {
            log.error("Error in forgotPassword: {}", e.getMessage());
            throw e;  // rethrow to handle at the controller level
        }
        return PasswordResetTokenResponse.builder().token(resetToken).build();
    }

    private void prepareAndDispatchResetPwdLink(String resetPasswordLink, String email) {
        String subject = "Reset Your Password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password. Please click the link below to create a new password. This link is valid for only 5 minutes for your security.</p>"
                + "<p>If you did not request this change, please ignore this email.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Reset My Password</a></p>"
                + "<p>For your safety, please do not share this link with anyone.</p>"
                + "<p>Thank you!</p>";
        dispatchEmail(subject,content,email);
    }

    @Override
    public String getURL(HttpServletRequest request) {String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        try {
            java.net.URL oldURL = new java.net.URL(siteURL);

            // URL for the local environment
            if ("localhost".equalsIgnoreCase(oldURL.getHost())) {
                return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), 3000, oldURL.getFile()).toString();
            }

            // URL for the production environment
            return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), oldURL.getFile()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct the correct URL", e);
        }
    }


}
