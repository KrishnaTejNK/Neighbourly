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
import org.springframework.stereotype.Service;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;

import java.util.List;

@Service
public class BookingRequestServiceImpl implements BookingRequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final AmenityRepository amenityRepository;

    public BookingRequestServiceImpl(
            BookingRequestRepository bookingRequestRepository,
            UserRepository userRepository,
            NeighbourhoodRepository neighbourhoodRepository,
            AmenityRepository amenityRepository) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.userRepository = userRepository;
        this.neighbourhoodRepository = neighbourhoodRepository;
        this.amenityRepository = amenityRepository;
    }

    // Fetch a User by ID
    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));
    }

    // Fetch a Neighbourhood by ID
    private Neighbourhood getNeighbourhoodById(int neighbourhoodId) {
        return neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found for ID: " + neighbourhoodId));
    }

    // Fetch a BookingRequest by ID
    private BookingRequest getBookingRequestById(int bookingId) {
        return bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException("Booking Request", bookingId, "Booking Request not found"));
    }

    // Fetch an Amenity by ID
    private Amenity getAmenityById(int amenityId) {
        return amenityRepository.findById(amenityId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException("Amenity", amenityId, "Amenity not found"));
    }

    @Override
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

    @Override
    public List<BookingRequest> getBookingsByNeighbourhood(int neighbourhoodId) {
        return bookingRequestRepository.findByNeighbourhood_id(neighbourhoodId);
    }

    @Override
    public List<BookingRequest> getBookingsByAmenity(int amenityId) {
        return bookingRequestRepository.findByAmenity_id(amenityId);
    }

    @Override
    public List<BookingRequest> getPendingRequests(int neighbourhoodId) {
        return bookingRequestRepository.findByNeighbourhood_idAndStatus(neighbourhoodId, BookingStatus.PENDING);
    }

    @Override
    public BookingRequest getRequestById(int bookingId) {
        return getBookingRequestById(bookingId);
    }

    @Transactional
    @Override
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
    @Override
    public boolean denyBooking(int bookingId) {
        BookingRequest request = getBookingRequestById(bookingId);
        request.setStatus(BookingStatus.REJECTED);
        bookingRequestRepository.save(request);

        return true;
    }
}
