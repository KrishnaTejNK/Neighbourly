package com.dalhousie.Neighbourly.booking.controller;

import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import com.dalhousie.Neighbourly.booking.service.BookingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking-requests")
public class BookingRequestController {

    private final BookingRequestService bookingRequestService;

    private static final int SUCCESS_STATUS = 201;

    @PostMapping("/create")
    public ResponseEntity<BookingRequest> createBookingRequest(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingRequest savedRequest = bookingRequestService.createBookingRequest(bookingRequestDTO);
        return ResponseEntity.status(SUCCESS_STATUS).body(savedRequest);
    }

    @GetMapping("/{neighbourhoodId}")
    public ResponseEntity<List<BookingRequest>> getBookingsByNeighbourhood(@PathVariable int neighbourhoodId) {
        List<BookingRequest> bookings = bookingRequestService.getPendingRequests(neighbourhoodId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/approve/{bookingId}")
    public ResponseEntity<String> approveBooking(@PathVariable int bookingId) {
        return bookingRequestService.approveBooking(bookingId)
                ? ResponseEntity.ok("Booking approved successfully.")
                : ResponseEntity.badRequest().body("Failed to approve booking.");
    }

    @PutMapping("/deny/{bookingId}")
    public ResponseEntity<String> denyBooking(@PathVariable int bookingId) {
        return bookingRequestService.denyBooking(bookingId)
                ? ResponseEntity.ok("Booking request denied.")
                : ResponseEntity.badRequest().body("Failed to deny booking.");
    }
}
