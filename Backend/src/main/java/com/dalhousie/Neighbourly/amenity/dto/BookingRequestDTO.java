package com.dalhousie.Neighbourly.amenity.dto;

import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDTO {
    private int bookingId;
    private Integer neighbourhood_id;
    private int user_id;
    private int amenityId;
    private String name;
    private String description;
    private LocalDateTime bookingFrom;
    private LocalDateTime bookingTo;
    private int expectedAttendees;
    private BookingRequest.BookingStatus status = BookingRequest.BookingStatus.PENDING;
}
