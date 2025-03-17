package com.dalhousie.Neighbourly.booking.entity;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking_requests")
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

    public BookingRequest(int bookingId, Integer neighbourhood_id, int user_id, int amenity_id, String name, String description, LocalDateTime bookingFrom, LocalDateTime bookingTo, int expectedAttendees, BookingStatus status) {
        this.bookingId = bookingId;
        this.neighbourhood_id = neighbourhood_id;
        this.user_id = user_id;
        this.amenity_id = amenity_id;
        this.name = name;
        this.description = description;
        this.bookingFrom = bookingFrom;
        this.bookingTo = bookingTo;
        this.expectedAttendees = expectedAttendees;
        this.status = status;
    }

    public BookingRequest() {

    }


    // Getters & Setters
    public enum BookingStatus {
        PENDING, APPROVED, REJECTED;
    }

}

