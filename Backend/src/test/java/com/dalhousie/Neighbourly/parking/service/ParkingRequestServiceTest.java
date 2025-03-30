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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingRequestServiceTest {

    private static final int RENTAL_ID = 1;
    private static final int OWNER_USER_ID = 1001;
    private static final int NEIGHBOURHOOD_ID = 2001;
    private static final int REQUESTER_USER_ID = 3001;
    private static final BigDecimal RENTAL_PRICE = BigDecimal.valueOf(20.00);
    private static final String SPOT_NAME = "Brunswick Street";
    private static final String USER_NAME = "John Doe";
    private static final String USER_EMAIL = "john.doe@example.com";

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
                .rentalId(RENTAL_ID)
                .userId(OWNER_USER_ID)
                .neighbourhoodId(NEIGHBOURHOOD_ID)
                .spot(SPOT_NAME)
                .price(RENTAL_PRICE)
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        user = User.builder()
                .id(REQUESTER_USER_ID)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .build();

        request = new ParkingRequest();
        request.setRequestId(RENTAL_ID);
        request.setParkingRental(rental);
        request.setUser(user);
        request.setStatus(ParkingRequest.ParkingRequestStatus.PENDING);

        requestDTO = new ParkingRequestDTO();
        requestDTO.setRentalId(RENTAL_ID);
        requestDTO.setUserId(REQUESTER_USER_ID);
    }

    @Test
    void testCreateParkingRequest() {
        when(parkingRentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(userRepository.findById(REQUESTER_USER_ID)).thenReturn(Optional.of(user));
        when(parkingRequestRepository.save(any(ParkingRequest.class))).thenReturn(request);

        parkingRequestService.createParkingRequest(requestDTO);

        verify(parkingRequestRepository, times(1)).save(any(ParkingRequest.class));
    }

    @Test
    void testGetParkingRequestsForOwner() {
        when(parkingRentalRepository.findByUserId(OWNER_USER_ID)).thenReturn(List.of(rental));
        when(parkingRequestRepository.findByParkingRental_RentalIdIn(List.of(RENTAL_ID))).thenReturn(List.of(request));

        List<ParkingResponseDTO> requests = parkingRequestService.getParkingRequestsForOwner(OWNER_USER_ID);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(ParkingRequest.ParkingRequestStatus.PENDING.name(), requests.get(0).getStatus());

        verify(parkingRequestRepository, times(1)).findByParkingRental_RentalIdIn(List.of(RENTAL_ID));
    }

    @Test
    void testApproveRequest() {
        when(parkingRequestRepository.findById(RENTAL_ID)).thenReturn(Optional.of(request));
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(rental);

        parkingRequestService.approveRequest(RENTAL_ID);

        assertEquals(ParkingRequest.ParkingRequestStatus.APPROVED, request.getStatus());
        assertEquals(ParkingRental.ParkingRentalStatus.BOOKED, rental.getStatus());

        verify(parkingRequestRepository, times(1)).save(request);
        verify(parkingRentalRepository, times(1)).save(rental);
    }

    @Test
    void testDenyRequest() {
        when(parkingRequestRepository.findById(RENTAL_ID)).thenReturn(Optional.of(request));

        parkingRequestService.denyRequest(RENTAL_ID);

        assertEquals(ParkingRequest.ParkingRequestStatus.DENIED, request.getStatus());

        verify(parkingRequestRepository, times(1)).save(request);
    }
}
