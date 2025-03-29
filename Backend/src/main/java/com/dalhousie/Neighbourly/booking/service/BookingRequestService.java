package com.dalhousie.Neighbourly.booking.service;

import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
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

    public BookingRequest createBookingRequest(BookingRequestDTO bookingRequestDTO) {
        BookingRequest bookingRequest = new BookingRequest();
        System.out.println(bookingRequestDTO);
        User user = userRepository.findById(bookingRequestDTO.getUser_id()).orElseThrow(() -> new RuntimeException("User not found for user"));
        Neighbourhood neighbourhood = neighbourhoodRepository.findById(bookingRequestDTO.getNeighbourhood_id())
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found"));



        bookingRequest.setUser_id(user.getId());
        bookingRequest.setNeighbourhood_id(neighbourhood.getNeighbourhoodId());
        bookingRequest.setAmenity_id(bookingRequestDTO.getAmenityId());
        bookingRequest.setName(bookingRequestDTO.getName());
        bookingRequest.setDescription(bookingRequestDTO.getDescription());
        bookingRequest.setBookingFrom(bookingRequestDTO.getBookingFrom());
        bookingRequest.setBookingTo(bookingRequestDTO.getBookingTo());
        bookingRequest.setExpectedAttendees(bookingRequestDTO.getExpectedAttendees());
        bookingRequest.setStatus(BookingRequest.BookingStatus.PENDING);

        return bookingRequestRepository.save(bookingRequest);
    }

    public List<BookingRequest> getBookingsByNeighbourhood(int neighbourhoodId) {
        return bookingRequestRepository.findByNeighbourhood_id(neighbourhoodId);
    }
    public List<BookingRequest> getBookingsByAmenity(int amenityId) {  // NEW METHOD
        return bookingRequestRepository.findByAmenity_id(amenityId);
    }

    @Autowired
    private AmenityRepository amenityRepository;

    public List<BookingRequest> getPendingRequests(int neighbourhoodId) {
        return  bookingRequestRepository.findByNeighbourhood_idAndStatus(neighbourhoodId, BookingRequest.BookingStatus.PENDING);
    }

    public BookingRequest getRequestById(int bookingId) {
        return bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException("Empty value",bookingId,"Amenity not found"));
    }



    @Transactional
    public boolean approveBooking(int bookingId) {
        BookingRequest request = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException ("Empty value",bookingId,"Amenity not found"));

        request.setStatus(BookingRequest.BookingStatus.APPROVED);
        bookingRequestRepository.save(request);

        Amenity amenity = amenityRepository.findById(request.getAmenity_id())
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException ("Empty value",request.getAmenity_id(),"Amenity not found"));

        amenity.setStatus(Amenity.Status.BOOKED);
        amenityRepository.save(amenity);

        return true;
    }

    @Transactional
    public boolean denyBooking(int bookingId) {
        BookingRequest request = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidConfigurationPropertyValueException ("Empty value",bookingId,"Amenity not found"));

        request.setStatus(BookingRequest.BookingStatus.REJECTED);
        bookingRequestRepository.save(request);

        return true;
    }
}

