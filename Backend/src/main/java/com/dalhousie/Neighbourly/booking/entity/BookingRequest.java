package com.dalhousie.Neighbourly.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking_requests")
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    @Column(nullable = true)
    private Integer neighbourhood_id;

    @Column(nullable = true)
    private int user_id;

    @Getter
    @Setter
    private int amenity_id;

    @Getter
    @Setter
    private String name; // Event Name

    @Getter
    @Setter
    private String description; // Event Description

    @Getter
    @Setter
    private LocalDateTime bookingFrom;

    @Getter
    @Setter
    private LocalDateTime bookingTo;

    @Getter
    @Setter
    private int expectedAttendees;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;


}

