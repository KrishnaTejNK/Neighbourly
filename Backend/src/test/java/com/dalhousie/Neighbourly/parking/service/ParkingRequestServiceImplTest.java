package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRentalStatus;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequest;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequestStatus;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import com.dalhousie.Neighbourly.parking.repository.ParkingRequestRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ParkingRequestServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class ParkingRequestServiceImplTest {

    @Mock
    private ParkingRequestRepository parkingRequestRepository;

    @Mock
    private ParkingRentalRepository parkingRentalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParkingRequestServiceImpl parkingRequestService;

    private User testUser;
    private ParkingRental testRental;
    private ParkingRequest testRequest;
    private ParkingRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");

        testRental = new ParkingRental();
        testRental.setRentalId(2);
        testRental.setUserId(3); // Owner
        testRental.setSpot("Spot A");
        testRental.setStatus(ParkingRentalStatus.AVAILABLE);

        testRequest = new ParkingRequest();
        testRequest.setRequestId(1);
        testRequest.setUser(testUser);
        testRequest.setParkingRental(testRental);
        testRequest.setStatus(ParkingRequestStatus.PENDING);

        testRequestDTO = new ParkingRequestDTO();
        testRequestDTO.setUserId(1);
        testRequestDTO.setRentalId(2);
    }

    @Test
    void createParkingRequest_successful() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(parkingRentalRepository.findById(2)).thenReturn(Optional.of(testRental));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);

        // Act
        parkingRequestService.createParkingRequest(testRequestDTO);

        // Assert
        verify(userRepository, times(1)).findById(1);
        verify(parkingRentalRepository, times(1)).findById(2);
        verify(parkingRequestRepository, times(1)).save(any(ParkingRequest.class));
    }


    @Test
    void getParkingRequestsForOwner_returnsRequests() {
        // Arrange
        int ownerId = 3;
        when(parkingRentalRepository.findByUserId(ownerId)).thenReturn(List.of(testRental));
        when(parkingRequestRepository.findByParkingRental_RentalIdIn(List.of(2))).thenReturn(List.of(testRequest));

        // Act
        List<ParkingResponseDTO> result = parkingRequestService.getParkingRequestsForOwner(ownerId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ParkingResponseDTO dto = result.get(0);
        assertEquals(1, dto.getRequestId());
        assertEquals(2, dto.getRentalId());
        assertEquals(1, dto.getUserId());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("Test User", dto.getName());
        assertEquals("Spot A", dto.getSpot());
        verify(parkingRentalRepository, times(1)).findByUserId(ownerId);
        verify(parkingRequestRepository, times(1)).findByParkingRental_RentalIdIn(List.of(2));
    }

    @Test
    void getParkingRequestsForOwner_noRentals_returnsEmptyList() {
        // Arrange
        int ownerId = 3;
        when(parkingRentalRepository.findByUserId(ownerId)).thenReturn(Collections.emptyList());

        // Act
        List<ParkingResponseDTO> result = parkingRequestService.getParkingRequestsForOwner(ownerId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(parkingRentalRepository, times(1)).findByUserId(ownerId);
        verify(parkingRequestRepository, never()).findByParkingRental_RentalIdIn(any());
    }

    @Test
    void approveRequest_successful() {
        // Arrange
        int requestId = 1;
        when(parkingRequestRepository.findById(requestId)).thenReturn(Optional.of(testRequest));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(testRental);

        // Act
        parkingRequestService.approveRequest(requestId);

        // Assert
        assertEquals(ParkingRequestStatus.APPROVED, testRequest.getStatus());
        assertEquals(ParkingRentalStatus.BOOKED, testRental.getStatus());
        verify(parkingRequestRepository, times(1)).findById(requestId);
        verify(parkingRequestRepository, times(1)).save(testRequest);
        verify(parkingRentalRepository, times(1)).save(testRental);
    }

    @Test
    void approveRequest_requestNotFound_throwsException() {
        // Arrange
        int requestId = 999;
        when(parkingRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parkingRequestService.approveRequest(requestId));
        assertEquals("Request not found", exception.getMessage());
        verify(parkingRequestRepository, times(1)).findById(requestId);
        verify(parkingRequestRepository, never()).save(any());
        verify(parkingRentalRepository, never()).save(any());
    }

    @Test
    void denyRequest_successful() {
        // Arrange
        int requestId = 1;
        when(parkingRequestRepository.findById(requestId)).thenReturn(Optional.of(testRequest));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);

        // Act
        parkingRequestService.denyRequest(requestId);

        // Assert
        assertEquals(ParkingRequestStatus.DENIED, testRequest.getStatus());
        verify(parkingRequestRepository, times(1)).findById(requestId);
        verify(parkingRequestRepository, times(1)).save(testRequest);
        verify(parkingRentalRepository, never()).save(any());
    }

    @Test
    void denyRequest_requestNotFound_throwsException() {
        // Arrange
        int requestId = 999;
        when(parkingRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parkingRequestService.denyRequest(requestId));
        assertEquals("Request not found", exception.getMessage());
        verify(parkingRequestRepository, times(1)).findById(requestId);
        verify(parkingRequestRepository, never()).save(any());
        verify(parkingRentalRepository, never()).save(any());
    }
}