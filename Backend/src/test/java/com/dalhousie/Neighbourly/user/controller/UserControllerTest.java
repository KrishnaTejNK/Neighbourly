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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class) // Enables Mockito support
@SpringBootTest
public class UserControllerTest {

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
        mockUser.setId(1);
        mockUser.setName("Krishna Tej");
        mockUser.setEmail("krishna@gmail.com");
        mockUser.setEmailVerified(true);
        mockUser.setContact("1234567890");
        mockUser.setNeighbourhood_id(101);
        mockUser.setAddress("123 Street");
        mockUser.setUserType(UserType.USER);
    }

    @Test
    void testGetUserProfileByEmail() throws Exception {
        when(userService.getUserByEmail("krishna@gmail.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/user/profile/krishna@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Krishna Tej"))
                .andExpect(jsonPath("$.email").value("krishna@gmail.com"))
                .andExpect(jsonPath("$.userType").value("USER"));
    }

    @Test
    void testGetUserProfileByUserId() throws Exception {
        when(userService.findUserById(1)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/user/details/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Krishna Tej"))
                .andExpect(jsonPath("$.email").value("krishna@gmail.com"))
                .andExpect(jsonPath("$.userType").value("USER"));
    }

    @Test
    void testGetUserProfileByEmail_NotFound() throws Exception {
        when(userService.getUserByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/profile/unknown@gmail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserProfileByUserId_NotFound() throws Exception {
        when(userService.findUserById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/details/99"))
                .andExpect(status().isNotFound());
    }
}
