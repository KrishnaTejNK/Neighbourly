package com.dalhousie.Neighbourly.user.respostiory;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;


    @BeforeEach
    void setUp() {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setName("Test Neighbourhood");
        neighbourhood.setLocation("Test Location");
        neighbourhood = neighbourhoodRepository.save(neighbourhood);

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .isEmailVerified(true)
                .contact("1234567890")
                .neighbourhood_id(neighbourhood.getNeighbourhoodId())
                .address("123 Street")
                .userType(UserType.USER)
                .build();
        userRepository.save(user);
    }

    @Test
    void testFindByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
    }

//    @Test
//    void testUpdatePassword() {
//        userRepository.updatePassword("john.doe@example.com", "newpassword");
//        Optional<User> updatedUser = userRepository.findByEmail("john.doe@example.com");
//        assertTrue(updatedUser.isPresent());
//        assertEquals("newpassword", updatedUser.get().getPassword());
//    }

    @Test
    void testFindByEmail_NotFound() {
        Optional<User> foundUser = userRepository.findByEmail("unknown@example.com");
        assertFalse(foundUser.isPresent());
    }
}