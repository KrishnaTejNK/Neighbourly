package com.dalhousie.Neighbourly.user.service;

import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample user for testing
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("Krishna Tej");
        mockUser.setEmail("krishna@gmail.com");
        mockUser.setEmailVerified(true);
        mockUser.setContact("1234567890");
        mockUser.setNeighbourhood_id(101);
        mockUser.setAddress("123 Street");
        mockUser.setUserType(UserType.USER);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        userRepository.save(mockUser);
    }
//    @Test
//    void testFindUserByEmail() {
//        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(mockUser));
//        Optional<User> foundUser = userService.findUserByEmail("john.doe@example.com");
//        assertTrue(foundUser.isPresent());
//        assertEquals("John Doe", foundUser.get().getName());
//    }

    @Test
    void testSaveUser() {
        userService.saveUser(mockUser);
        verify(userRepository, times(1)).save(mockUser);
    }

//    @Test
//    void testFindUserById() {
//        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
//        Optional<User> foundUser = userService.findUserById(1);
//        assertTrue(foundUser.isPresent());
//        assertEquals("John Doe", foundUser.get().getName());
//    }
//
//    @Test
//    void testUpdatePassword() {
//        doNothing().when(userRepository).updatePassword("john.doe@example.com", "newpassword");
//        userService.updatePassword("john.doe@example.com", "newpassword");
//        verify(userRepository, times(1)).updatePassword("john.doe@example.com", "newpassword");
//    }
//
//    @Test
//    void testGetUserByEmail() {
//        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(mockUser));
//        Optional<User> foundUser = userService.getUserByEmail("john.doe@example.com");
//        assertTrue(foundUser.isPresent());
//        assertEquals("John Doe", foundUser.get().getName());
//    }
}