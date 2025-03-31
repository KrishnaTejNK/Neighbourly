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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setUserType(UserType.USER);
        testUser.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void isUserPresent_userExists_returnsTrue() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserPresent("test@example.com");

        assertTrue(result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void isUserPresent_userDoesNotExist_returnsFalse() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean result = userService.isUserPresent("test@example.com");

        assertFalse(result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void findUserByEmail_userExists_returnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void saveUser_savesSuccessfully() {
        userService.saveUser(testUser);

        verify(userRepository, times(EXPECTED_CALL_COUNT)).save(testUser);
    }

    @Test
    void findUserById_userExists_returnsUser() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserById(TEST_USER_ID);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
    }

    @Test
    void updatePassword_updatesSuccessfully() {
        userService.updatePassword("test@example.com", "newPassword");

        verify(userRepository, times(EXPECTED_CALL_COUNT)).updatePassword("test@example.com", "newPassword");
    }

    @Test
    void getUserRole_userExists_returnsUserRole() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserType result = userService.getUserRole("test@example.com");

        assertEquals(UserType.USER, result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void getUserRole_userDoesNotExist_returnsDefaultRole() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UserType result = userService.getUserRole("test@example.com");

        assertEquals(UserType.USER, result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_userExists_returnsUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
    }

    @Test
    void getUsersByNeighbourhood_returnsUserList() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNeighbourhood_id(TEST_NEIGHBOURHOOD_ID)).thenReturn(users);

        List<User> result = userService.getUsersByNeighbourhood(TEST_NEIGHBOURHOOD_ID);

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
    }
}