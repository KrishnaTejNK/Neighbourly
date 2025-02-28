package com.dalhousie.Neighbourly.authentication.entity;


import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String otp;

    private Instant expiryDate;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;
}
