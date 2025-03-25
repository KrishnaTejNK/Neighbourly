package com.dalhousie.Neighbourly.authentication.entity;


import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "otp")
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Integer id;

    private String otp;

    private Instant expiryDate;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;


    public Otp(String otp) {
        this.otp = otp;
    }
}
