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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyValueException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingRequestServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class BookingRequestServiceImplTest {

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int TEST_AMENITY_ID = 1;
    private static final int TEST_BOOKING_ID = 1;
    private static final int MISSING_BOOKING_ID = 999;
    private static final int EXPECTED_CALL_COUNT = 1;
    private static final int BOOKING_DURATION_HOURS = 2;
    private static final int EXPECTED_ATTENDEES = 10;

    @Mock
    private BookingRequestRepository bookingRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private BookingRequestServiceImpl bookingRequestService;

    private User mockUser;
    private Neighbourhood mockNeighbourhood;
    private Amenity mockAmenity;
    private BookingRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(TEST_USER_ID);

        mockNeighbourhood = new Neighbourhood();
        mockNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        mockAmenity = new Amenity();
        mockAmenity.setAmenityId(TEST_AMENITY_ID);
        mockAmenity.setStatus(Status.AVAILABLE);

        mockRequest = new BookingRequest();
        mockRequest.setBookingId(TEST_BOOKING_ID);
        mockRequest.setUser_id(TEST_USER_ID);
        mockRequest.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
        mockRequest.setAmenity_id(TEST_AMENITY_ID);
        mockRequest.setStatus(BookingStatus.PENDING);
    }

    @Test
    void createBookingRequest_createsAndReturnsRequest() {
        // Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setUser_id(TEST_USER_ID);
        dto.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
        dto.setAmenityId(TEST_AMENITY_ID);
        dto.setName("Event");
        dto.setDescription("Test event");
        dto.setBookingFrom(LocalDateTime.now());
        dto.setBookingTo(LocalDateTime.now().plusHours(BOOKING_DURATION_HOURS));
        dto.setExpectedAttendees(EXPECTED_ATTENDEES);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.of(mockNeighbourhood));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);

        // Act
        BookingRequest result = bookingRequestService.createBookingRequest(dto);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUser_id());
        assertEquals(TEST_NEIGHBOURHOOD_ID, result.getNeighbourhood_id());
        assertEquals(TEST_AMENITY_ID, result.getAmenity_id());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).save(any(BookingRequest.class));
    }

    @Test
    void createBookingRequest_throwsException_whenUserNotFound() {
        // Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setUser_id(TEST_USER_ID);
        dto.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingRequestService.createBookingRequest(dto));
        assertEquals("User not found for ID: " + TEST_USER_ID, exception.getMessage());
    }

    @Test
    void getBookingsByNeighbourhood_returnsBookings() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_id(TEST_NEIGHBOURHOOD_ID)).thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getBookingsByNeighbourhood(TEST_NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockRequest, result.get(0));
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void getBookingsByAmenity_returnsBookings() {
        // Arrange
        when(bookingRequestRepository.findByAmenity_id(TEST_AMENITY_ID)).thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getBookingsByAmenity(TEST_AMENITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockRequest, result.get(0));
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).findByAmenity_id(TEST_AMENITY_ID);
    }

    @Test
    void getPendingRequests_returnsPendingRequests() {
        // Arrange
        when(bookingRequestRepository.findByNeighbourhood_idAndStatus(TEST_NEIGHBOURHOOD_ID, BookingStatus.PENDING))
                .thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getPendingRequests(TEST_NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.PENDING, result.get(0).getStatus());
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhood_idAndStatus(TEST_NEIGHBOURHOOD_ID, BookingStatus.PENDING);
    }

    @Test
    void getRequestById_returnsRequest() {
        // Arrange
        when(bookingRequestRepository.findById(TEST_BOOKING_ID)).thenReturn(Optional.of(mockRequest));

        // Act
        BookingRequest result = bookingRequestService.getRequestById(TEST_BOOKING_ID);

        // Assert
        assertNotNull(result);
        assertEquals(mockRequest, result);
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_BOOKING_ID);
    }

    @Test
    void getRequestById_throwsException_whenNotFound() {
        // Arrange
        when(bookingRequestRepository.findById(MISSING_BOOKING_ID)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidConfigurationPropertyValueException exception = assertThrows(InvalidConfigurationPropertyValueException.class,
                () -> bookingRequestService.getRequestById(MISSING_BOOKING_ID));
        assertEquals("Booking Request not found", exception.getReason());
    }

    @Test
    void approveBooking_approvesRequestAndUpdatesAmenity() {
        // Arrange
        when(bookingRequestRepository.findById(TEST_BOOKING_ID)).thenReturn(Optional.of(mockRequest));
        when(amenityRepository.findById(TEST_AMENITY_ID)).thenReturn(Optional.of(mockAmenity));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(mockAmenity);

        // Act
        boolean result = bookingRequestService.approveBooking(TEST_BOOKING_ID);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, mockRequest.getStatus());
        assertEquals(Status.BOOKED, mockAmenity.getStatus());
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).save(mockRequest);
        verify(amenityRepository, times(EXPECTED_CALL_COUNT)).save(mockAmenity);
    }

    @Test
    void denyBooking_deniesRequest() {
        // Arrange
        when(bookingRequestRepository.findById(TEST_BOOKING_ID)).thenReturn(Optional.of(mockRequest));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);

        // Act
        boolean result = bookingRequestService.denyBooking(TEST_BOOKING_ID);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.REJECTED, mockRequest.getStatus());
        verify(bookingRequestRepository, times(EXPECTED_CALL_COUNT)).save(mockRequest);
        verify(amenityRepository, never()).save(any()); // Amenity not updated in deny
    }
}