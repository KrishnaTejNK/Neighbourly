package com.dalhousie.Neighbourly.booking.service;

import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;

import java.util.List;

public interface BookingRequestService {
    BookingRequest createBookingRequest(BookingRequestDTO bookingRequestDTO);
    List<BookingRequest> getBookingsByNeighbourhood(int neighbourhoodId);
    List<BookingRequest> getBookingsByAmenity(int amenityId);
    List<BookingRequest> getPendingRequests(int neighbourhoodId);
    BookingRequest getRequestById(int bookingId);
    boolean approveBooking(int bookingId);
    boolean denyBooking(int bookingId);
}
