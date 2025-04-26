package com.dalhousie.Neighbourly.parking.entity;

import com.dalhousie.Neighbourly.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "parking_requests")
public class ParkingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private ParkingRental parkingRental;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ParkingRequestStatus status = ParkingRequestStatus.PENDING; // Default status



    // Getters and Setters
}
