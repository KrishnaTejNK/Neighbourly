package com.dalhousie.Neighbourly.authentication.service;

import com.dalhousie.Neighbourly.authentication.entity.Otp;
import com.dalhousie.Neighbourly.authentication.entity.PasswordReset;
import com.dalhousie.Neighbourly.authentication.jwt.JwtService;
import com.dalhousie.Neighbourly.authentication.requestEntity.AuthenticateRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.OtpVerificationRequest;
import com.dalhousie.Neighbourly.authentication.requestEntity.RegisterRequest;
import com.dalhousie.Neighbourly.authentication.responseEntity.AuthenticationResponse;
import com.dalhousie.Neighbourly.authentication.responseEntity.PasswordResetTokenResponse;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of AuthenticationService for handling user authentication and related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int LOCALHOST_PORT = 3000;
    private static final int EXPIRATION_TIME_MINUTES = 5;

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
        checkIfUserExists(registerRequest.getEmail());
        User user = createUser(registerRequest);
        userService.saveUser(user);
        log.info("User entered");

        sendOtpForUser(user);
        return AuthenticationResponse.builder().token(null).build();
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticateRequest authenticateRequest) {
        User user = validateUserAndAuthenticate(authenticateRequest);
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify before logging in.");
        }

        String jwtToken = jwtService.generateToken(user, user.isEmailVerified());
        return AuthenticationResponse.builder().token(jwtToken).user(user).build();
    }

    @Override
    @Transactional
    public void resendOtp(String email) {
        User user = getUserByEmail(email);
        Otp otp = otpService.resendOtp(user.getId());
        prepareAndDispatchOtpMail(otp.getOtp(), user.getEmail());
    }

    @Override
    public AuthenticationResponse verifyOtp(OtpVerificationRequest otpVerificationRequest) {
        Otp otp = findAndValidateOtp(otpVerificationRequest.getOtp());
        User user = getUserById(otp.getUserId());
        user.setEmailVerified(true);

        String jwtToken = jwtService.generateToken(user, user.isEmailVerified());
        otpService.deleteOtp(otp);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private void checkIfUserExists(String email) {
        if (userService.isUserPresent(email)) {
            throw new RuntimeException("Provided user already exists");
        }
    }

    private User createUser(RegisterRequest registerRequest) {
        return User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .userType(UserType.USER)
                .build();
    }

    private User validateUserAndAuthenticate(AuthenticateRequest authenticateRequest) {
        if (!userService.isUserPresent(authenticateRequest.getEmail())) {
            throw new UsernameNotFoundException("User not found with email: " + authenticateRequest.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticateRequest.getEmail(), authenticateRequest.getPassword()));

        return userService.findUserByEmail(authenticateRequest.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + authenticateRequest.getEmail()));
    }

    private User getUserByEmail(String email) {
        return userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private Otp findAndValidateOtp(String otpValue) {
        Optional<Otp> otpOptional = otpService.findByOtp(otpValue);
        if (otpOptional.isEmpty() || !otpService.isOtpValid(otpOptional.get())) {
            throw new OtpServiceImpl.TokenExpiredException("Invalid or expired OTP. Please try again.");
        }
        return otpOptional.get();
    }

    private User getUserById(int userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private void sendOtpForUser(User user) {
        Otp otp = otpService.generateOtp(user.getId());
        prepareAndDispatchOtpMail(otp.getOtp(), user.getEmail());
    }

    private void prepareAndDispatchOtpMail(String otp, String mail) {
        String subject = "Verify Your Email";
        String content = String.format("<p>Hello,</p><p>Your OTP for email verification is:</p><h2>%s</h2><p>This OTP is valid for %d minutes.</p>", otp, EXPIRATION_TIME_MINUTES);
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
        User user = getUserByEmail(email);
        PasswordReset passwordReset = validateAndRetrieveResetToken(user.getId(), token);
        updateUserPassword(email, password);
        resetTokenService.deleteResetPasswordToken(passwordReset);
    }

    private PasswordReset validateAndRetrieveResetToken(int userId, String token) {
        PasswordReset passwordReset = resetTokenService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User did not initiate a reset password request."));

        validatePasswordResetToken(token, passwordReset);
        return passwordReset;
    }

    private void validatePasswordResetToken(String token, PasswordReset passwordReset) {
        if (!Objects.equals(token, passwordReset.getToken())) {
            throw new RuntimeException("Failed to authenticate token. Please request to reset your password again.");
        }

        if (!resetTokenService.isTokenValid(passwordReset)) {
            throw new RuntimeException("Token expired. Please request to reset your password again.");
        }
    }

    private void updateUserPassword(String email, String password) {
        String newPassword = passwordEncoder.encode(password);
        userService.updatePassword(email, newPassword);
    }

    @Override
    public PasswordResetTokenResponse forgotPassword(String email, String resetUrl) {
        String resetToken = generateResetToken(email);
        String resetPasswordLink = buildResetPasswordLink(resetUrl, email);
        prepareAndDispatchResetPwdLink(resetPasswordLink, email);
        return PasswordResetTokenResponse.builder().token(resetToken).build();
    }

    private String generateResetToken(String email) {
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return resetTokenService.createResetPasswordToken(user.getId()).getToken();
    }

    private String buildResetPasswordLink(String resetUrl, String email) {
        String resetPasswordLink = resetUrl + "?email=" + email;
        log.info("Reset password link: {}", resetPasswordLink);
        return resetPasswordLink;
    }

    private void prepareAndDispatchResetPwdLink(String resetPasswordLink, String email) {
        String subject = "Reset Your Password";
        String content = String.format(
                "<p>Hello,</p><p>You have requested to reset your password. Please click the link below to create a new password. This link is valid for only %d minutes for your security.</p><p>If you did not request this change, please ignore this email.</p><p>Click the link below to reset your password:</p><p><a href=\"%s\">Reset My Password</a></p><p>For your safety, please do not share this link with anyone.</p><p>Thank you!</p>",
                EXPIRATION_TIME_MINUTES, resetPasswordLink);
        dispatchEmail(subject, content, email);
    }

    @Override
    public String getURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString().replace(request.getServletPath(), "");
        try {
            java.net.URL oldURL = new java.net.URL(siteURL);
            if ("localhost".equalsIgnoreCase(oldURL.getHost())) {
                return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), LOCALHOST_PORT, oldURL.getFile()).toString();
            }
            return new java.net.URL(oldURL.getProtocol(), oldURL.getHost(), oldURL.getFile()).toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct the correct URL", e);
        }
    }
}