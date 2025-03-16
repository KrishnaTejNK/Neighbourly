package com.dalhousie.Neighbourly.booking.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import com.dalhousie.Neighbourly.amenity.dto.BookingRequestDTO;
import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingRequestServiceTest {

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
        user.setId(1);
        user.setName("John Doe");

        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(101);
        neighbourhood.setName("Greenwood");

        amenity = new Amenity(1, 101, "Community Hall",
                Timestamp.valueOf("2025-03-15 10:00:00"),
                Timestamp.valueOf("2025-03-15 20:00:00"),
                Amenity.Status.AVAILABLE);

        bookingRequestDTO = new BookingRequestDTO();
        bookingRequestDTO.setUser_id(1);
        bookingRequestDTO.setNeighbourhood_id(101);
        bookingRequestDTO.setAmenityId(1);
        bookingRequestDTO.setName("Birthday Party");
        bookingRequestDTO.setDescription("Booking for a birthday celebration");
        bookingRequestDTO.setBookingFrom(Timestamp.valueOf("2025-03-15 12:00:00").toLocalDateTime());
        bookingRequestDTO.setBookingTo(Timestamp.valueOf("2025-03-15 16:00:00").toLocalDateTime());
        bookingRequestDTO.setExpectedAttendees(50);

        bookingRequest = new BookingRequest();
        bookingRequest.setBookingId(1);
        bookingRequest.setUser_id(1);
        bookingRequest.setNeighbourhood_id(101);
        bookingRequest.setAmenity_id(1);
        bookingRequest.setName("Birthday Party");
        bookingRequest.setDescription("Booking for a birthday celebration");
        bookingRequest.setBookingFrom(Timestamp.valueOf("2025-03-15 12:00:00").toLocalDateTime());
        bookingRequest.setBookingTo(Timestamp.valueOf("2025-03-15 16:00:00").toLocalDateTime());
        bookingRequest.setExpectedAttendees(50);
        bookingRequest.setStatus(BookingRequest.BookingStatus.PENDING);
    }

    @Test
    void testCreateBookingRequest() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(101)).thenReturn(Optional.of(neighbourhood));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(bookingRequest);

        // Act
        BookingRequest savedBooking = bookingRequestService.createBookingRequest(bookingRequestDTO);

        // Assert
        assertNotNull(savedBooking);
        assertEquals(1, savedBooking.getUser_id());
        assertEquals(101, savedBooking.getNeighbourhood_id());
        assertEquals(1, savedBooking.getAmenity_id());
        assertEquals("Birthday Party", savedBooking.getName());
        verify(bookingRequestRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void testGetBookingsByNeighbourhood() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_id(101)).thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> bookings = bookingRequestService.getBookingsByNeighbourhood(101);

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("Birthday Party", bookings.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_id(101);
    }

    @Test
    void testGetBookingsByAmenity() {
        // Arrange
        when(bookingRequestRepository.findByAmenity_id(1)).thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> bookings = bookingRequestService.getBookingsByAmenity(1);

        // Assert
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("Birthday Party", bookings.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByAmenity_id(1);
    }

    @Test
    void testApproveBooking() {
        // Arrange
        when(bookingRequestRepository.findById(1)).thenReturn(Optional.of(bookingRequest));
        when(amenityRepository.findById(1)).thenReturn(Optional.of(amenity));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(bookingRequest);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(amenity);

        // Act
        boolean result = bookingRequestService.approveBooking(1);

        // Assert
        assertTrue(result);
        assertEquals(BookingRequest.BookingStatus.APPROVED, bookingRequest.getStatus());
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
        assertEquals(BookingRequest.BookingStatus.REJECTED, bookingRequest.getStatus());
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
        assertEquals(1, foundRequest.getUser_id ());
        assertEquals("Birthday Party", foundRequest.getName());
        verify(bookingRequestRepository, times(1)).findById(1);
    }

    @Test
    void testGetPendingRequests() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_idAndStatus(101, "PENDING")).thenReturn(Arrays.asList(bookingRequest));

        // Act
        List<BookingRequest> pendingRequests = bookingRequestService.getPendingRequests(101);

        // Assert
        assertNotNull(pendingRequests);
        assertEquals(1, pendingRequests.size());
        assertEquals("Birthday Party", pendingRequests.get(0).getName());
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_idAndStatus(101, "PENDING");
    }
}
