package com.dalhousie.Neighbourly.user.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        assertNotNull(user);
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1, "John Doe", "john.doe@example.com", "password", true, "1234567890", 101, "123 Street", UserType.USER);
        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isEmailVerified());
        assertEquals("1234567890", user.getContact());
        assertEquals(101, user.getNeighbourhood_id());
        assertEquals("123 Street", user.getAddress());
        assertEquals(UserType.USER, user.getUserType());
    }

    @Test
    void testBuilder() {
        User user = User.builder()
                .id(1)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .isEmailVerified(true)
                .contact("1234567890")
                .neighbourhood_id(101)
                .address("123 Street")
                .userType(UserType.USER)
                .build();

        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isEmailVerified());
        assertEquals("1234567890", user.getContact());
        assertEquals(101, user.getNeighbourhood_id());
        assertEquals("123 Street", user.getAddress());
        assertEquals(UserType.USER, user.getUserType());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setEmailVerified(true);
        user.setContact("1234567890");
        user.setNeighbourhood_id(101);
        user.setAddress("123 Street");
        user.setUserType(UserType.USER);

        assertEquals(1, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertTrue(user.isEmailVerified());
        assertEquals("1234567890", user.getContact());
        assertEquals(101, user.getNeighbourhood_id());
        assertEquals("123 Street", user.getAddress());
        assertEquals(UserType.USER, user.getUserType());
    }

    @Test
    void testUserDetailsMethods() {
        User user = User.builder()
                .email("john.doe@example.com")
                .build();

        assertEquals("john.doe@example.com", user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }
}