package com.dalhousie.Neighbourly.booking.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import com.dalhousie.Neighbourly.booking.entity.BookingStatus;
import com.dalhousie.Neighbourly.booking.repository.BookingRequestRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRequestServiceTest {

    // Constants for magic numbers
    private static final int USER_ID = 1;
    private static final int NEIGHBOURHOOD_ID = 101;
    private static final int AMENITY_ID = 1;
    private static final String USER_NAME = "John Doe";
    private static final String NEIGHBOURHOOD_NAME = "Greenwood";
    private static final String AMENITY_NAME = "Community Hall";
    private static final String EVENT_NAME = "Birthday Party";
    private static final String EVENT_DESCRIPTION = "Booking for a birthday celebration";
    private static final LocalDateTime BOOKING_FROM = LocalDateTime.of(2025, 3, 15, 12, 0, 0, 0);
    private static final LocalDateTime BOOKING_TO = LocalDateTime.of(2025, 3, 15, 16, 0, 0, 0);
    private static final int EXPECTED_ATTENDEES = 50;

    @Mock
    private BookingRequestRepository bookingRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private BookingRequestService bookingRequestService;

    private BookingRequest bookingRequest;
    private BookingRequestDTO bookingRequestDTO;
    private User user;
    private Neighbourhood neighbourhood;
    private Amenity amenity;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setName(USER_NAME);

        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(NEIGHBOURHOOD_ID);
        neighbourhood.setName(NEIGHBOURHOOD_NAME);

        amenity = new Amenity(AMENITY_ID, NEIGHBOURHOOD_ID, AMENITY_NAME,
                Timestamp.valueOf(BOOKING_FROM),
                Timestamp.valueOf(BOOKING_TO),
                Amenity.Status.AVAILABLE);

        bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setUser_id(USER_ID);
        bookingRequestDTO.setNeighbourhood_id(NEIGHBOURHOOD_ID);
        bookingRequestDTO.setAmenityId(AMENITY_ID);
        bookingRequestDTO.setName(EVENT_NAME);
        bookingRequestDTO.setDescription(EVENT_DESCRIPTION);
        bookingRequestDTO.setBookingFrom(BOOKING_FROM);
        bookingRequestDTO.setBookingTo(BOOKING_TO);
        bookingRequestDTO.setExpectedAttendees(EXPECTED_ATTENDEES);

        bookingRequest = new BookingRequest();
        bookingRequest.setBookingId(1);
        bookingRequest.setUser_id(USER_ID);
        bookingRequest.setNeighbourhood_id(NEIGHBOURHOOD_ID);
        bookingRequest.setAmenity_id(AMENITY_ID);
        bookingRequest.setName(EVENT_NAME);
        bookingRequest.setDescription(EVENT_DESCRIPTION);
        bookingRequest.setBookingFrom(BOOKING_FROM);
        bookingRequest.setBookingTo(BOOKING_TO);
        bookingRequest.setExpectedAttendees(EXPECTED_ATTENDEES);
        bookingRequest.setStatus(BookingStatus.PENDING);
    }

    @Test
    void testCreateBookingRequest() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(NEIGHBOURHOOD_ID)).thenReturn(Optional.of(neighbourhood));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(bookingRequest);

        // Act
        BookingRequest savedBooking = bookingRequestService.createBookingRequest(bookingRequestDTO);

        // Assert
        assertNotNull(savedBooking);
        assertEquals(USER_ID, savedBooking.getUser_id());
        assertEquals(NEIGHBOURHOOD_ID, savedBooking.getNeighbourhood_id());
        assertEquals(AMENITY_ID, savedBooking.getAmenity_id());
        assertEquals(EVENT_NAME, savedBooking.getName());
        verify(bookingRequestRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void testGetBookingsByNeighbourhood() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_id(NEIGHBOURHOOD_ID)).thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> bookings = bookingRequestService.getBookingsByNeighbourhood(NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(EVENT_NAME, bookings.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_id(NEIGHBOURHOOD_ID);
    }

    @Test
    void testGetBookingsByAmenity() {
        // Arrange
        when(bookingRequestRepository.findByAmenity_id(AMENITY_ID)).thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> bookings = bookingRequestService.getBookingsByAmenity(AMENITY_ID);

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(EVENT_NAME, bookings.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByAmenity_id(AMENITY_ID);
    }

    @Test
    void testApproveBooking() {
        // Arrange
        when(bookingRequestRepository.findById(1)).thenReturn(Optional.of(bookingRequest));
        when(amenityRepository.findById(AMENITY_ID)).thenReturn(Optional.of(amenity));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(bookingRequest);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        // Act
        boolean result = bookingRequestService.approveBooking(1);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, bookingRequest.getStatus());
        assertEquals(Amenity.Status.BOOKED, amenity.getStatus());
        verify(bookingRequestRepository, times(1)).save(any(BookingRequest.class));
        verify(amenityRepository, times(1)).save(any(Amenity.class));
    }

    @Test
    void testDenyBooking() {
        // Arrange
        when(bookingRequestRepository.findById(1)).thenReturn(Optional.of(bookingRequest));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(bookingRequest);

        // Act
        boolean result = bookingRequestService.denyBooking(1);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.REJECTED, bookingRequest.getStatus());
        verify(bookingRequestRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void testGetRequestById() {
        // Arrange
        when(bookingRequestRepository.findById(1)).thenReturn(Optional.of(bookingRequest));

        // Act
        BookingRequest foundRequest = bookingRequestService.getRequestById(1);

        // Assert
        assertNotNull(foundRequest);
        assertEquals(USER_ID, foundRequest.getUser_id());
        assertEquals(EVENT_NAME, foundRequest.getName());
        verify(bookingRequestRepository, times(1)).findById(1);
    }

    @Test
    void testGetPendingRequests() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_idAndStatus(NEIGHBOURHOOD_ID, BookingStatus.PENDING))
                .thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> pendingRequests = bookingRequestService.getPendingRequests(NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(pendingRequests);
        assertEquals(1, pendingRequests.size());
        assertEquals(EVENT_NAME, pendingRequests.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_idAndStatus(NEIGHBOURHOOD_ID, BookingStatus.PENDING);
    }
}
