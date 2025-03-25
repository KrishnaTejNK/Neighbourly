package com.dalhousie.Neighbourly.user.service;

import java.util.List;
import java.util.Optional;

import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;


public interface UserService {

    boolean isUserPresent(String email);

    Optional<User> findUserByEmail(String email);

    void saveUser(User user);

    Optional<User> findUserById(int id);

    void updatePassword(String email, String password);

     UserType getUserRole(String email);

    Optional<User> getUserByEmail(String email);

    List<User> getUsersByNeighbourhood(int neighbourhoodId);
}
