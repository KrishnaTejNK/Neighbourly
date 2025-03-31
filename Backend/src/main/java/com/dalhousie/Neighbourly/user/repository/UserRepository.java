package com.dalhousie.Neighbourly.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dalhousie.Neighbourly.user.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.email = :email")
    void updatePassword(String email, String newPassword);

    // Count total members in a neighborhood (excluding admins)
    @Query("SELECT COUNT(u) FROM User u WHERE u.neighbourhood_id = :neighbourhoodId")
    long countByNeighbourhoodId(Integer neighbourhoodId);

    // Fetch the community manager for a neighborhood
    @Query("SELECT u.name FROM User u WHERE u.neighbourhood_id = :neighbourhoodId AND u.userType = 'COMMUNITY_MANAGER'")
    String findManagerNameByNeighbourhoodId(Integer neighbourhoodId);

    @Query("SELECT u.id FROM User u WHERE u.neighbourhood_id = :neighbourhoodId AND u.userType = 'COMMUNITY_MANAGER'")
    String userRepositoryFindManagerIdByNeighbourhoodId(int neighbourhoodId);

    @Query("SELECT u FROM User u WHERE u.neighbourhood_id = :neighbourhoodId")
    List<User> findByNeighbourhood_id(int neighbourhoodId);

}
