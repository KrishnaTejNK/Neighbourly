package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequest;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingRequestServiceTest {

    @Mock
    private ParkingRequestRepository parkingRequestRepository;
    @Mock
    private ParkingRentalRepository parkingRentalRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParkingRequestService parkingRequestService;

    private ParkingRental rental;
    private User user;
    private ParkingRequest request;
    private ParkingRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        rental = ParkingRental.builder()
                .rentalId(1)
                .userId(1001) // Owner of the rental
                .neighbourhoodId(2001)
                .spot("brunswik street")
                .price(BigDecimal.valueOf(20.00))
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        user = User.builder()
                .id(3001)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        request = new ParkingRequest();
        request.setRequestId(1);
        request.setParkingRental(rental);
        request.setUser(user);
        request.setStatus(ParkingRequest.ParkingRequestStatus.PENDING);

        requestDTO = new ParkingRequestDTO();
        requestDTO.setRentalId(1);
        requestDTO.setUserId(3001);
    }

    @Test
    void testCreateParkingRequest() {
        when(parkingRentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(userRepository.findById(3001)).thenReturn(Optional.of(user));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(request);

        parkingRequestService.createParkingRequest(requestDTO);

        verify(parkingRequestRepository, times(1)).save(any(ParkingRequest.class));
    }

    @Test
    void testGetParkingRequestsForOwner() {
        when(parkingRentalRepository.findByUserId(1001)).thenReturn(List.of(rental));
        when(parkingRequestRepository.findByParkingRental_RentalIdIn(List.of(1))).thenReturn(List.of(request));

        List<ParkingResponseDTO> requests = parkingRequestService.getParkingRequestsForOwner(1001);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(ParkingRequest.ParkingRequestStatus.PENDING.name(), requests.get(0).getStatus());

        verify(parkingRequestRepository, times(1)).findByParkingRental_RentalIdIn(List.of(1));
    }

    @Test
    void testApproveRequest() {
        when(parkingRequestRepository.findById(1)).thenReturn(Optional.of(request));
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(rental);

        parkingRequestService.approveRequest(1);

        assertEquals(ParkingRequest.ParkingRequestStatus.APPROVED, request.getStatus());
        assertEquals(ParkingRental.ParkingRentalStatus.BOOKED, rental.getStatus());

        verify(parkingRequestRepository, times(1)).save(request);
        verify(parkingRentalRepository, times(1)).save(rental);
    }

    @Test
    void testDenyRequest() {
        when(parkingRequestRepository.findById(1)).thenReturn(Optional.of(request));

        parkingRequestService.denyRequest(1);

        assertEquals(ParkingRequest.ParkingRequestStatus.DENIED, request.getStatus());

        verify(parkingRequestRepository, times(1)).save(request);
    }
}
