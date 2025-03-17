package com.dalhousie.Neighbourly.booking.controller;

import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import com.dalhousie.Neighbourly.booking.service.BookingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-requests")
public class BookingRequestController {
    @Autowired
    private BookingRequestService bookingRequestService;

    @PostMapping("/create")
    public ResponseEntity<BookingRequest> createBookingRequest(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingRequest savedRequest = bookingRequestService.createBookingRequest(bookingRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping("/{neighbourhoodId}")
    public ResponseEntity<List<BookingRequest>> getBookingsByNeighbourhood(@PathVariable int neighbourhoodId) {
        return ResponseEntity.ok(bookingRequestService.getPendingRequests(neighbourhoodId));
    }
    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<String> approveBooking(@PathVariable int bookingId) {
        boolean success = bookingRequestService.approveBooking(bookingId);
        if (success) {
            return ResponseEntity.ok("Booking approved successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to approve booking.");
        }
    }

    @PutMapping("/deny/{bookingId}")
    public ResponseEntity<String> denyBooking(@PathVariable int bookingId) {
        boolean success = bookingRequestService.denyBooking(bookingId);
        if (success) {
            return ResponseEntity.ok("Booking request denied.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to deny booking.");
        }
    }


}

