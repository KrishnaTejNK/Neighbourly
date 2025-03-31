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

    @Mock
    private ParkingRentalRepository parkingRentalRepository;

    @InjectMocks
    private ParkingRentalServiceImpl parkingRentalService;

    private ParkingRentalDTO testDto;
    private ParkingRental testRental;

    @BeforeEach
    void setUp() {
        testDto = new ParkingRentalDTO();
        testDto.setNeighbourhoodId(1);
        testDto.setUserId(1);
        testDto.setSpot("A1");
        testDto.setStartTime(LocalDateTime.of(2025, 4, 1, 10, 0));
        testDto.setEndTime(LocalDateTime.of(2025, 4, 1, 12, 0));
        testDto.setPrice(BigDecimal.valueOf(10.0));

        testRental = ParkingRental.builder()
                .rentalId(1)
                .neighbourhoodId(1)
                .userId(1)
                .spot("A1")
                .startTime(LocalDateTime.of(2025, 4, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 4, 1, 12, 0))
                .price(BigDecimal.valueOf(10.0))
                .status(ParkingRentalStatus.AVAILABLE)
                .build();
    }

    @Test
    void getAvailableParkingRentals_returnsAvailableRentals() {
        List<ParkingRental> expectedRentals = List.of(testRental);
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(1, ParkingRentalStatus.AVAILABLE))
                .thenReturn(expectedRentals);

        List<ParkingRental> result = parkingRentalService.getAvailableParkingRentals(1);

        assertEquals(1, result.size());
        assertEquals(testRental, result.get(0));
        verify(parkingRentalRepository).findByNeighbourhoodIdAndStatus(1, ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void getAvailableParkingRentals_noRentals_returnsEmptyList() {
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(1, ParkingRentalStatus.AVAILABLE))
                .thenReturn(List.of());

        List<ParkingRental> result = parkingRentalService.getAvailableParkingRentals(1);

        assertTrue(result.isEmpty());
        verify(parkingRentalRepository).findByNeighbourhoodIdAndStatus(1, ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void createParkingRental_createsAndReturnsRental() {
        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(testRental);

        ParkingRental result = parkingRentalService.createParkingRental(testDto);

        assertNotNull(result);
        assertEquals(testRental.getRentalId(), result.getRentalId());
        assertEquals(testDto.getNeighbourhoodId(), result.getNeighbourhoodId());
        assertEquals(testDto.getUserId(), result.getUserId());
        assertEquals(testDto.getSpot(), result.getSpot());
        assertEquals(testDto.getStartTime(), result.getStartTime());
        assertEquals(testDto.getEndTime(), result.getEndTime());
        assertEquals(testDto.getPrice(), result.getPrice());
        assertEquals(ParkingRentalStatus.AVAILABLE, result.getStatus());
        verify(parkingRentalRepository).save(argThat(rental ->
                rental.getNeighbourhoodId() == 1 &&
                        rental.getUserId() == 1 &&
                        rental.getSpot().equals("A1") &&
                        rental.getStatus() == ParkingRentalStatus.AVAILABLE
        ));
    }

    @Test
    void buildParkingRental_createsCorrectEntity() {
        ParkingRental result = parkingRentalService.createParkingRental(testDto);

        // Since buildParkingRental is private, we test it through createParkingRental
        verify(parkingRentalRepository).save(argThat(rental ->
                rental.getNeighbourhoodId() == testDto.getNeighbourhoodId() &&
                        rental.getUserId() == testDto.getUserId() &&
                        rental.getSpot().equals(testDto.getSpot()) &&
                        rental.getStartTime().equals(testDto.getStartTime()) &&
                        rental.getEndTime().equals(testDto.getEndTime()) &&
                        rental.getPrice() == testDto.getPrice() &&
                        rental.getStatus() == ParkingRentalStatus.AVAILABLE
        ));
    }
}