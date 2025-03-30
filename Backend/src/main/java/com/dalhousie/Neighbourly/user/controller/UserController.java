package com.dalhousie.Neighbourly.user.controller;

import com.dalhousie.Neighbourly.user.dto.UserResponse;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.service.UserService;
import com.dalhousie.Neighbourly.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{email}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String email) {
        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isEmailVerified(user.isEmailVerified())
                .contact(user.getContact())
                .neighbourhoodId(user.getNeighbourhood_id())
                .address(user.getAddress())
                .userType(user.getUserType())
                .build();

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable int userId) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isEmailVerified(user.isEmailVerified())
                .contact(user.getContact())
                .neighbourhoodId(user.getNeighbourhood_id())
                .address(user.getAddress())
                .userType(user.getUserType())
                .build();

        return ResponseEntity.ok(userResponse);
    }

    // Get user role by email
    @GetMapping("/role/{email}")
    public ResponseEntity<String> getUserRole(@PathVariable String email) {
        UserType role = userService.getUserRole(email);
        String responseBody = "{\"role\": \"" + role + "\"}";
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{neighbourhoodId}")
    public List<UserDTO> getResidentsByNeighbourhood(@PathVariable int neighbourhoodId) {
        List<User> residents = userService.getUsersByNeighbourhood(neighbourhoodId);
        return residents.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }
}