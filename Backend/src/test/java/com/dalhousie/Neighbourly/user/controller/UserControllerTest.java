package com.dalhousie.Neighbourly.user.controller;

import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Enables Mockito support
@SpringBootTest
public class UserControllerTest {

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 101;
    private static final int MISSING_USER_ID = 99;
    private static final String TEST_CONTACT = "1234567890";
    private static final String TEST_ADDRESS = "123 Street";
    private static final int EXPECTED_CALL_COUNT = 1;

    private MockMvc mockMvc;

    @Mock  // Replaces @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        // Sample user for testing
        mockUser = new User();
        mockUser.setId(TEST_USER_ID);
        mockUser.setName("Krishna Tej");
        mockUser.setEmail("krishna@gmail.com");
        mockUser.setEmailVerified(true);
        mockUser.setContact(TEST_CONTACT);
        mockUser.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
        mockUser.setAddress(TEST_ADDRESS);
        mockUser.setUserType(UserType.USER);
    }

    @Test
    void testGetUserProfileByUserId() throws Exception {
        when(userService.findUserById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/user/details/" + TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.name").value("Krishna Tej"))
                .andExpect(jsonPath("$.email").value("krishna@gmail.com"))
                .andExpect(jsonPath("$.userType").value("USER"));

        verify(userService, times(EXPECTED_CALL_COUNT)).findUserById(TEST_USER_ID);
    }

    @Test
    void testGetUserProfileByEmail_NotFound() throws Exception {
        when(userService.getUserByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/profile/unknown@gmail.com"))
                .andExpect(status().isNotFound());

        verify(userService, times(EXPECTED_CALL_COUNT)).getUserByEmail("unknown@gmail.com");
    }

    @Test
    void testGetUserProfileByUserId_NotFound() throws Exception {
        when(userService.findUserById(MISSING_USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/details/" + MISSING_USER_ID))
                .andExpect(status().isNotFound());

        verify(userService, times(EXPECTED_CALL_COUNT)).findUserById(MISSING_USER_ID);
    }
}