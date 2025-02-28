package com.dalhousie.Neighbourly.user.service;


import java.util.Optional;
import org.springframework.stereotype.Service;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

//    isUserPresent
//                -> true when the user is present
//                -> false when the user is absent
    public boolean isUserPresent(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public void updatePassword(String email, String password) {
        userRepository.updatePassword(email, password);
    }

    
}

