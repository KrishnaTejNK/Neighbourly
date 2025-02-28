package com.dalhousie.Neighbourly.user.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean isEmailVerified = false;  // Default to false

    @Column(nullable = true)
    private String contact;

    @Column(nullable = true)
    private Integer neighbourhood_id;  // Nullable if user hasn't joined a community

    @Column(nullable = true)
    private String address;  // Nullable for non-residents

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;  // Enum to store USER, RESIDENT, COMMUNITY_MANAGER, ADMIN

    // Security Overrides (Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // No specific authorities
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
