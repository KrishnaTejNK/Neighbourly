package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingRentalServiceTest {

    @Mock
    private ParkingRentalRepository parkingRentalRepository;

    @InjectMocks
    private ParkingRentalService parkingRentalService;

    private ParkingRental rental1, rental2;
    private ParkingRentalDTO rentalDTO;

    @BeforeEach
    void setUp() {
        rental1 = ParkingRental.builder()
                .rentalId(1)
                .neighbourhoodId(1001)
                .userId(2001)
                .spotNumber(String.valueOf(10))
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .price(BigDecimal.valueOf(15.00))
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        rental2 = ParkingRental.builder()
                .rentalId(2)
                .neighbourhoodId(1001)
                .userId(2002)
                .spotNumber("20")
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(3))
                .price(BigDecimal.valueOf(20.00))
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        rentalDTO = new ParkingRentalDTO();
        rentalDTO.setNeighbourhoodId(1001);
        rentalDTO.setUserId(2003);
        rentalDTO.setSpotNumber(String.valueOf(30));
        rentalDTO.setStartTime(LocalDateTime.now().plusDays(3));
        rentalDTO.setEndTime(LocalDateTime.now().plusDays(4));
        rentalDTO.setPrice(BigDecimal.valueOf(25.00));
    }

    @Test
    void testGetAvailableParkingRentals() {
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(1001, ParkingRental.ParkingRentalStatus.AVAILABLE))
                .thenReturn(Arrays.asList(rental1, rental2));

        List<ParkingRental> availableRentals = parkingRentalService.getAvailableParkingRentals(1001);

        assertNotNull(availableRentals);
        assertEquals(2, availableRentals.size());
        assertEquals("10", availableRentals.get(0).getSpotNumber());
        assertEquals("20", availableRentals.get(1).getSpotNumber());

        verify(parkingRentalRepository, times(1))
                .findByNeighbourhoodIdAndStatus(1001, ParkingRental.ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void testCreateParkingRental() {
        ParkingRental savedRental = ParkingRental.builder()
                .rentalId(3)
                .neighbourhoodId(rentalDTO.getNeighbourhoodId())
                .userId(rentalDTO.getUserId())
                .spotNumber(rentalDTO.getSpotNumber())
                .startTime(rentalDTO.getStartTime())
                .endTime(rentalDTO.getEndTime())
                .price(rentalDTO.getPrice())
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(savedRental);

        ParkingRental result = parkingRentalService.createParkingRental(rentalDTO);

        assertNotNull(result);
        assertEquals(3, result.getRentalId());
        assertEquals("30", result.getSpotNumber());
        assertEquals(BigDecimal.valueOf(25.00), result.getPrice());

        verify(parkingRentalRepository, times(1)).save(any(ParkingRental.class));
    }
}
