package com.dalhousie.Neighbourly.booking.service;

import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.entity.Status;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import com.dalhousie.Neighbourly.booking.entity.BookingStatus;
import com.dalhousie.Neighbourly.booking.repository.BookingRequestRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingRequestService {
    @Autowired
    private BookingRequestRepository bookingRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    // Helper method to fetch a User by ID
    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for user " + userId));
    }

    // Helper method to fetch a Neighbourhood by ID
    private Neighbourhood getNeighbourhoodById(int neighbourhoodId) {
        return neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found for neighbourhood " + neighbourhoodId));
    }

    // Helper method to fetch a BookingRequest by ID
    private BookingRequest getBookingRequestById(int bookingId) {
        return bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException("Empty value", bookingId, "Booking Request not found"));
    }

    // Helper method to fetch an Amenity by ID
    private Amenity getAmenityById(int amenityId) {
        return amenityRepository.findById(amenityId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException("Empty value", amenityId, "Amenity not found"));
    }

    // Refactor: Create BookingRequest with necessary DTO
    public BookingRequest createBookingRequest(BookingRequestDTO bookingRequestDTO) {
        User user = getUserById(bookingRequestDTO.getUser_id());
        Neighbourhood neighbourhood = getNeighbourhoodById(bookingRequestDTO.getNeighbourhood_id());

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser_id(user.getId());
        bookingRequest.setNeighbourhood_id(neighbourhood.getNeighbourhoodId());
        bookingRequest.setAmenity_id(bookingRequestDTO.getAmenityId());
        bookingRequest.setName(bookingRequestDTO.getName());
        bookingRequest.setDescription(bookingRequestDTO.getDescription());
        bookingRequest.setBookingFrom(bookingRequestDTO.getBookingFrom());
        bookingRequest.setBookingTo(bookingRequestDTO.getBookingTo());
        bookingRequest.setExpectedAttendees(bookingRequestDTO.getExpectedAttendees());
        bookingRequest.setStatus(BookingStatus.PENDING);

        return bookingRequestRepository.save(bookingRequest);
    }

    // Refactor: Get bookings by neighborhood
    public List<BookingRequest> getBookingsByNeighbourhood(int neighbourhoodId) {
        return bookingRequestRepository.findByNeighbourhood_id(neighbourhoodId);
    }

    // Refactor: Get bookings by amenity
    public List<BookingRequest> getBookingsByAmenity(int amenityId) {
        return bookingRequestRepository.findByAmenity_id(amenityId);
    }

    // Refactor: Get pending booking requests by neighbourhood
    public List<BookingRequest> getPendingRequests(int neighbourhoodId) {
        return bookingRequestRepository.findByNeighbourhood_idAndStatus(neighbourhoodId, BookingStatus.PENDING);
    }

    // Refactor: Get a booking request by ID
    public BookingRequest getRequestById(int bookingId) {
        return getBookingRequestById(bookingId);
    }

    @Transactional
    // Refactor: Approve Booking Request
    public boolean approveBooking(int bookingId) {
        BookingRequest request = getBookingRequestById(bookingId);
        request.setStatus(BookingStatus.APPROVED);
        bookingRequestRepository.save(request);

        Amenity amenity = getAmenityById(request.getAmenity_id());
        amenity.setStatus(Status.BOOKED);
        amenityRepository.save(amenity);

        return true;
    }

    @Transactional
    // Refactor: Deny Booking Request
    public boolean denyBooking(int bookingId) {
        BookingRequest request = getBookingRequestById(bookingId);
        request.setStatus(BookingStatus.REJECTED);
        bookingRequestRepository.save(request);

        return true;
    }
}
