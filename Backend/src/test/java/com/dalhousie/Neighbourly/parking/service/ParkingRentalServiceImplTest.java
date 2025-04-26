package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRentalStatus;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingRentalServiceImplTest {

    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_RENTAL_ID = 1;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;
    private static final String TEST_SPOT = "A1";
    private static final LocalDateTime TEST_START_TIME = LocalDateTime.of(2025, 4, 1, 10, 0);
    private static final LocalDateTime TEST_END_TIME = LocalDateTime.of(2025, 4, 1, 12, 0);
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(10.0);

    @Mock
    private ParkingRentalRepository parkingRentalRepository;

    @InjectMocks
    private ParkingRentalServiceImpl parkingRentalService;

    private ParkingRentalDTO testDto;
    private ParkingRental testRental;

    @BeforeEach
    void setUp() {
        testDto = new ParkingRentalDTO();
        testDto.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        testDto.setUserId(TEST_USER_ID);
        testDto.setSpot(TEST_SPOT);
        testDto.setStartTime(TEST_START_TIME);
        testDto.setEndTime(TEST_END_TIME);
        testDto.setPrice(TEST_PRICE);

        testRental = ParkingRental.builder()
                .rentalId(TEST_RENTAL_ID)
                .neighbourhoodId(TEST_NEIGHBOURHOOD_ID)
                .userId(TEST_USER_ID)
                .spot(TEST_SPOT)
                .startTime(TEST_START_TIME)
                .endTime(TEST_END_TIME)
                .price(TEST_PRICE)
                .status(ParkingRentalStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAvailableParkingRentals_returnsAvailableRentals() {
        List<ParkingRental> expectedRentals = List.of(testRental);
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(TEST_NEIGHBOURHOOD_ID, ParkingRentalStatus.AVAILABLE))
                .thenReturn(expectedRentals);

        List<ParkingRental> result = parkingRentalService.getAvailableParkingRentals(TEST_NEIGHBOURHOOD_ID);

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        assertEquals(testRental, result.get(0));
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhoodIdAndStatus(TEST_NEIGHBOURHOOD_ID, ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void getAvailableParkingRentals_noRentals_returnsEmptyList() {
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(TEST_NEIGHBOURHOOD_ID, ParkingRentalStatus.AVAILABLE))
                .thenReturn(List.of());

        List<ParkingRental> result = parkingRentalService.getAvailableParkingRentals(TEST_NEIGHBOURHOOD_ID);

        assertTrue(result.isEmpty());
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhoodIdAndStatus(TEST_NEIGHBOURHOOD_ID, ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void createParkingRental_createsAndReturnsRental() {
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(testRental);

        ParkingRental result = parkingRentalService.createParkingRental(testDto);

        assertNotNull(result);
        assertEquals(TEST_RENTAL_ID, result.getRentalId());
        assertEquals(testDto.getNeighbourhoodId(), result.getNeighbourhoodId());
        assertEquals(testDto.getUserId(), result.getUserId());
        assertEquals(testDto.getSpot(), result.getSpot());
        assertEquals(testDto.getStartTime(), result.getStartTime());
        assertEquals(testDto.getEndTime(), result.getEndTime());
        assertEquals(testDto.getPrice(), result.getPrice());
        assertEquals(ParkingRentalStatus.AVAILABLE, result.getStatus());
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).save(argThat(rental ->
                rental.getNeighbourhoodId() == TEST_NEIGHBOURHOOD_ID &&
                        rental.getUserId() == TEST_USER_ID &&
                        rental.getSpot().equals(TEST_SPOT) &&
                        rental.getStatus() == ParkingRentalStatus.AVAILABLE
        ));
    }

    @Test
    void buildParkingRental_createsCorrectEntity() {
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(testRental);

        ParkingRental result = parkingRentalService.createParkingRental(testDto);

        // Since buildParkingRental is private, we test it through createParkingRental
        verify(parkingRentalRepository, times(EXPECTED_CALL_COUNT)).save(argThat(rental ->
                rental.getNeighbourhoodId() == testDto.getNeighbourhoodId() &&
                        rental.getUserId() == testDto.getUserId() &&
                        rental.getSpot().equals(testDto.getSpot()) &&
                        rental.getStartTime().equals(testDto.getStartTime()) &&
                        rental.getEndTime().equals(testDto.getEndTime()) &&
                        rental.getPrice().equals(testDto.getPrice()) &&
                        rental.getStatus() == ParkingRentalStatus.AVAILABLE
        ));
    }
}