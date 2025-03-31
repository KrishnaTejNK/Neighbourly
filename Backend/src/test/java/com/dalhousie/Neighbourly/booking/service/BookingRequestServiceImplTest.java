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
        mockUser.setId(1);

        mockNeighbourhood = new Neighbourhood();
        mockNeighbourhood.setNeighbourhoodId(1);

        mockAmenity = new Amenity();
        mockAmenity.setAmenityId(1);
        mockAmenity.setStatus(Status.AVAILABLE);

        mockRequest = new BookingRequest();
        mockRequest.setBookingId(1);
        mockRequest.setUser_id(1);
        mockRequest.setNeighbourhood_id(1);
        mockRequest.setAmenity_id(1);
        mockRequest.setStatus(BookingStatus.PENDING);
    }

    @Test
    void createBookingRequest_createsAndReturnsRequest() {
        // Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setUser_id(1);
        dto.setNeighbourhood_id(1);
        dto.setAmenityId(1);
        dto.setName("Event");
        dto.setDescription("Test event");
        dto.setBookingFrom(LocalDateTime.now());
        dto.setBookingTo(LocalDateTime.now().plusHours(2));
        dto.setExpectedAttendees(10);

        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.findById(1)).thenReturn(Optional.of(mockNeighbourhood));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);

        // Act
        BookingRequest result = bookingRequestService.createBookingRequest(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals(1, result.getNeighbourhood_id());
        assertEquals(1, result.getAmenity_id());
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRequestRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void createBookingRequest_throwsException_whenUserNotFound() {
        // Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setUser_id(1);
        dto.setNeighbourhood_id(1);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bookingRequestService.createBookingRequest(dto));
        assertEquals("User not found for ID: 1", exception.getMessage());
    }

    @Test
    void getBookingsByNeighbourhood_returnsBookings() {
        // Arrange
        int neighbourhoodId = 1;
        when(bookingRequestRepository.findByNeighbourhood_id(neighbourhoodId)).thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getBookingsByNeighbourhood(neighbourhoodId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockRequest, result.get(0));
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_id(neighbourhoodId);
    }

    @Test
    void getBookingsByAmenity_returnsBookings() {
        // Arrange
        int amenityId = 1;
        when(bookingRequestRepository.findByAmenity_id(amenityId)).thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getBookingsByAmenity(amenityId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockRequest, result.get(0));
        verify(bookingRequestRepository, times(1)).findByAmenity_id(amenityId);
    }

    @Test
    void getPendingRequests_returnsPendingRequests() {
        // Arrange
        int neighbourhoodId = 1;
        when(bookingRequestRepository.findByNeighbourhood_idAndStatus(neighbourhoodId, BookingStatus.PENDING))
                .thenReturn(List.of(mockRequest));

        // Act
        List<BookingRequest> result = bookingRequestService.getPendingRequests(neighbourhoodId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.PENDING, result.get(0).getStatus());
        verify(bookingRequestRepository, times(1)).findByNeighbourhood_idAndStatus(neighbourhoodId, BookingStatus.PENDING);
    }

    @Test
    void getRequestById_returnsRequest() {
        // Arrange
        int bookingId = 1;
        when(bookingRequestRepository.findById(bookingId)).thenReturn(Optional.of(mockRequest));

        // Act
        BookingRequest result = bookingRequestService.getRequestById(bookingId);

        // Assert
        assertNotNull(result);
        assertEquals(mockRequest, result);
        verify(bookingRequestRepository, times(1)).findById(bookingId);
    }

    @Test
    void getRequestById_throwsException_whenNotFound() {
        // Arrange
        int bookingId = 999;
        when(bookingRequestRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidConfigurationPropertyValueException exception = assertThrows(InvalidConfigurationPropertyValueException.class,
                () -> bookingRequestService.getRequestById(bookingId));
        assertEquals("Booking Request not found", exception.getReason());
    }

    @Test
    void approveBooking_approvesRequestAndUpdatesAmenity() {
        // Arrange
        int bookingId = 1;
        when(bookingRequestRepository.findById(bookingId)).thenReturn(Optional.of(mockRequest));
        when(amenityRepository.findById(1)).thenReturn(Optional.of(mockAmenity));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(mockAmenity);

        // Act
        boolean result = bookingRequestService.approveBooking(bookingId);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.APPROVED, mockRequest.getStatus());
        assertEquals(Status.BOOKED, mockAmenity.getStatus());
        verify(bookingRequestRepository, times(1)).save(mockRequest);
        verify(amenityRepository, times(1)).save(mockAmenity);
    }

    @Test
    void denyBooking_deniesRequest() {
        // Arrange
        int bookingId = 1;
        when(bookingRequestRepository.findById(bookingId)).thenReturn(Optional.of(mockRequest));
        when(bookingRequestRepository.save(any(BookingRequest.class))).thenReturn(mockRequest);

        // Act
        boolean result = bookingRequestService.denyBooking(bookingId);

        // Assert
        assertTrue(result);
        assertEquals(BookingStatus.REJECTED, mockRequest.getStatus());
        verify(bookingRequestRepository, times(1)).save(mockRequest);
        verify(amenityRepository, never()).save(any()); // Amenity not updated in deny
    }
}