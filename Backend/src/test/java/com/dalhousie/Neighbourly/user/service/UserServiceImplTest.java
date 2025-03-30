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

    private static final String USER_EMAIL = "test@example.com";
    private static final String PASSWORD = "securePassword123";
    private static final int USER_ID = 1001;
    private static final int NEIGHBOURHOOD_ID = 2001;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .password(PASSWORD)
                .userType(UserType.ADMIN)
                .build();
    }

    @Test
    void isUserPresent_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));

        boolean result = userService.isUserPresent(USER_EMAIL);

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void isUserPresent_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        boolean result = userService.isUserPresent(USER_EMAIL);

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void findUserByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserByEmail(USER_EMAIL);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().getId());
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void findUserByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserByEmail(USER_EMAIL);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void saveUser_ShouldSaveUserSuccessfully() {
        userService.saveUser(testUser);

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void findUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findUserById(USER_ID);

        assertTrue(result.isPresent());
        assertEquals(USER_EMAIL, result.get().getEmail());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void findUserById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Optional<User> result = userService.findUserById(USER_ID);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    void updatePassword_ShouldUpdatePasswordSuccessfully() {
        doNothing().when(userRepository).updatePassword(USER_EMAIL, PASSWORD);

        userService.updatePassword(USER_EMAIL, PASSWORD);

        verify(userRepository, times(1)).updatePassword(USER_EMAIL, PASSWORD);
    }

    @Test
    void getUserRole_ShouldReturnUserRole_WhenUserExists() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));

        UserType result = userService.getUserRole(USER_EMAIL);

        assertEquals(UserType.ADMIN, result);
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void getUserRole_ShouldReturnDefaultUserRole_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        UserType result = userService.getUserRole(USER_EMAIL);

        assertEquals(UserType.USER, result); // Default role should be USER
        verify(userRepository, times(1)).findByEmail(USER_EMAIL);
    }

    @Test
    void getUsersByNeighbourhood_ShouldReturnListOfUsers() {
        when(userRepository.findByNeighbourhood_id(NEIGHBOURHOOD_ID)).thenReturn(List.of(testUser));

        List<User> result = userService.getUsersByNeighbourhood(NEIGHBOURHOOD_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(USER_ID, result.get(0).getId());
        verify(userRepository, times(1)).findByNeighbourhood_id(NEIGHBOURHOOD_ID);
    }
}
