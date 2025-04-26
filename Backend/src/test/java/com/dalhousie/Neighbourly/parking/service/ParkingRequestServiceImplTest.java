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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_RENTAL_ID = 2;
    private static final int TEST_OWNER_ID = 3;
    private static final int TEST_REQUEST_ID = 1;
    private static final int MISSING_REQUEST_ID = 999;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

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
        testUser.setId(TEST_USER_ID);
        testUser.setName("Test User");

        testRental = new ParkingRental();
        testRental.setRentalId(TEST_RENTAL_ID);
        testRental.setUserId(TEST_OWNER_ID); // Owner
        testRental.setSpot("Spot A");
        testRental.setStatus(ParkingRentalStatus.AVAILABLE);

        testRequest = new ParkingRequest();
        testRequest.setRequestId(TEST_REQUEST_ID);
        testRequest.setUser(testUser);
        testRequest.setParkingRental(testRental);
        testRequest.setStatus(ParkingRequestStatus.PENDING);

        testRequestDTO = new ParkingRequestDTO();
        testRequestDTO.setUserId(TEST_USER_ID);
        testRequestDTO.setRentalId(TEST_RENTAL_ID);
    }

    @Test
    void createParkingRequest_successful() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(parkingRentalRepository.findById(TEST_RENTAL_ID)).thenReturn(Optional.of(testRental));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);

        // Act
        parkingRequestService.createParkingRequest(testRequestDTO);

        // Assert
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_RENTAL_ID);
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).save(any(ParkingRequest.class));
    }

    @Test
    void getParkingRequestsForOwner_returnsRequests() {
        // Arrange
        when(parkingRentalRepository.findByUserId(TEST_OWNER_ID)).thenReturn(List.of(testRental));
        when(parkingRequestRepository.findByParkingRental_RentalIdIn(List.of(TEST_RENTAL_ID))).thenReturn(List.of(testRequest));

        // Act
        List<ParkingResponseDTO> result = parkingRequestService.getParkingRequestsForOwner(TEST_OWNER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_LIST_SIZE, result.size());
        ParkingResponseDTO dto = result.get(0);
        assertEquals(TEST_REQUEST_ID, dto.getRequestId());
        assertEquals(TEST_RENTAL_ID, dto.getRentalId());
        assertEquals(TEST_USER_ID, dto.getUserId());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("Test User", dto.getName());
        assertEquals("Spot A", dto.getSpot());
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).findByUserId(TEST_OWNER_ID);
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).findByParkingRental_RentalIdIn(List.of(TEST_RENTAL_ID));
    }

    @Test
    void getParkingRequestsForOwner_noRentals_returnsEmptyList() {
        // Arrange
        when(parkingRentalRepository.findByUserId(TEST_OWNER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<ParkingResponseDTO> result = parkingRequestService.getParkingRequestsForOwner(TEST_OWNER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).findByUserId(TEST_OWNER_ID);
        verify(parkingRequestRepository, never()).findByParkingRental_RentalIdIn(any());
    }

    @Test
    void approveRequest_successful() {
        // Arrange
        when(parkingRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(testRental);

        // Act
        parkingRequestService.approveRequest(TEST_REQUEST_ID);

        // Assert
        assertEquals(ParkingRequestStatus.APPROVED, testRequest.getStatus());
        assertEquals(ParkingRentalStatus.BOOKED, testRental.getStatus());
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REQUEST_ID);
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).save(testRequest);
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).save(testRental);
    }

    @Test
    void approveRequest_requestNotFound_throwsException() {
        // Arrange
        when(parkingRequestRepository.findById(MISSING_REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parkingRequestService.approveRequest(MISSING_REQUEST_ID));
        assertEquals("Request not found", exception.getMessage());
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).findById(MISSING_REQUEST_ID);
        verify(parkingRequestRepository, never()).save(any());
        verify(parkingRentalRepository, never()).save(any());
    }

    @Test
    void denyRequest_successful() {
        // Arrange
        when(parkingRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(testRequest);

        // Act
        parkingRequestService.denyRequest(TEST_REQUEST_ID);

        // Assert
        assertEquals(ParkingRequestStatus.DENIED, testRequest.getStatus());
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REQUEST_ID);
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).save(testRequest);
        verify(parkingRentalRepository, never()).save(any());
    }

    @Test
    void denyRequest_requestNotFound_throwsException() {
        // Arrange
        when(parkingRequestRepository.findById(MISSING_REQUEST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> parkingRequestService.denyRequest(MISSING_REQUEST_ID));
        assertEquals("Request not found", exception.getMessage());
        verify(parkingRequestRepository, times(EXPECTED_CALL_COUNT)).findById(MISSING_REQUEST_ID);
        verify(parkingRequestRepository, never()).save(any());
        verify(parkingRentalRepository, never()).save(any());
    }
}