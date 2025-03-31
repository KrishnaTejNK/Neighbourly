package com.dalhousie.Neighbourly.user.service;

import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setUserType(UserType.USER);
        testUser.setNeighbourhood_id(1);
    }

    @Test
    void isUserPresent_userExists_returnsTrue() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserPresent("test@example.com");

        assertTrue(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void isUserPresent_userDoesNotExist_returnsFalse() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean result = userService.isUserPresent("test@example.com");

        assertFalse(result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findUserByEmail_userExists_returnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void saveUser_savesSuccessfully() {
        userService.saveUser(testUser);

        verify(userRepository).save(testUser);
    }

    @Test
    void findUserById_userExists_returnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserById(1);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(1);
    }

    @Test
    void updatePassword_updatesSuccessfully() {
        userService.updatePassword("test@example.com", "newPassword");

        verify(userRepository).updatePassword("test@example.com", "newPassword");
    }

    @Test
    void getUserRole_userExists_returnsUserRole() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserType result = userService.getUserRole("test@example.com");

        assertEquals(UserType.USER, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserRole_userDoesNotExist_returnsDefaultRole() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UserType result = userService.getUserRole("test@example.com");

        assertEquals(UserType.USER, result);
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_userExists_returnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUsersByNeighbourhood_returnsUserList() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNeighbourhood_id(1)).thenReturn(users);

        List<User> result = userService.getUsersByNeighbourhood(1);

        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findByNeighbourhood_id(1);
    }
}