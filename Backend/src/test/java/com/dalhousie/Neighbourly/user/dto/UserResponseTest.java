package com.dalhousie.Neighbourly.user.dto;

import com.dalhousie.Neighbourly.user.entity.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserResponseTest {

    @Test
    void testNoArgsConstructor() {
        UserResponse userResponse = new UserResponse();
        assertNotNull(userResponse);
    }

    @Test
    void testAllArgsConstructor() {
        UserResponse userResponse = new UserResponse(1, "John Doe", "john.doe@example.com", true, "1234567890", 101, "123 Street", UserType.USER);
        assertEquals(1, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertTrue(userResponse.isEmailVerified());
        assertEquals("1234567890", userResponse.getContact());
        assertEquals(101, userResponse.getNeighbourhoodId());
        assertEquals("123 Street", userResponse.getAddress());
        assertEquals(UserType.USER, userResponse.getUserType());
    }

    @Test
    void testBuilder() {
        UserResponse userResponse = UserResponse.builder()
                .id(1)
                .name("John Doe")
                .email("john.doe@example.com")
                .isEmailVerified(true)
                .contact("1234567890")
                .neighbourhoodId(101)
                .address("123 Street")
                .userType(UserType.USER)
                .build();

        assertEquals(1, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertTrue(userResponse.isEmailVerified());
        assertEquals("1234567890", userResponse.getContact());
        assertEquals(101, userResponse.getNeighbourhoodId());
        assertEquals("123 Street", userResponse.getAddress());
        assertEquals(UserType.USER, userResponse.getUserType());
    }

    @Test
    void testSettersAndGetters() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1);
        userResponse.setName("John Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setEmailVerified(true);
        userResponse.setContact("1234567890");
        userResponse.setNeighbourhoodId(101);
        userResponse.setAddress("123 Street");
        userResponse.setUserType(UserType.USER);

        assertEquals(1, userResponse.getId());
        assertEquals("John Doe", userResponse.getName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertTrue(userResponse.isEmailVerified());
        assertEquals("1234567890", userResponse.getContact());
        assertEquals(101, userResponse.getNeighbourhoodId());
        assertEquals("123 Street", userResponse.getAddress());
        assertEquals(UserType.USER, userResponse.getUserType());
    }
}