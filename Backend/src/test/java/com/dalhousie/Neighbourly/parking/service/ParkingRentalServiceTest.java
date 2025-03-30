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

    private static final int NEIGHBOURHOOD_ID = 1001;
    private static final int USER_ID_1 = 2001;
    private static final int USER_ID_2 = 2002;
    private static final int USER_ID_3 = 2003;
    private static final int RENTAL_ID_1 = 1;
    private static final int RENTAL_ID_2 = 2;
    private static final int RENTAL_ID_3 = 3;
    private static final String SPOT_10 = "10";
    private static final String SPOT_20 = "20";
    private static final String SPOT_30 = "30";
    private static final BigDecimal PRICE_15 = BigDecimal.valueOf(15.00);
    private static final BigDecimal PRICE_20 = BigDecimal.valueOf(20.00);
    private static final BigDecimal PRICE_25 = BigDecimal.valueOf(25.00);

    @Mock
    private ParkingRentalRepository parkingRentalRepository;

    @InjectMocks
    private ParkingRentalService parkingRentalService;

    private ParkingRental rental1, rental2;
    private ParkingRentalDTO rentalDTO;

    @BeforeEach
    void setUp() {
        rental1 = ParkingRental.builder()
                .rentalId(RENTAL_ID_1)
                .neighbourhoodId(NEIGHBOURHOOD_ID)
                .userId(USER_ID_1)
                .spot(SPOT_10)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .price(PRICE_15)
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        rental2 = ParkingRental.builder()
                .rentalId(RENTAL_ID_2)
                .neighbourhoodId(NEIGHBOURHOOD_ID)
                .userId(USER_ID_2)
                .spot(SPOT_20)
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(3))
                .price(PRICE_20)
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        rentalDTO = new ParkingRentalDTO();
        rentalDTO.setNeighbourhoodId(NEIGHBOURHOOD_ID);
        rentalDTO.setUserId(USER_ID_3);
        rentalDTO.setSpot(SPOT_30);
        rentalDTO.setStartTime(LocalDateTime.now().plusDays(3));
        rentalDTO.setEndTime(LocalDateTime.now().plusDays(4));
        rentalDTO.setPrice(PRICE_25);
    }

    @Test
    void testGetAvailableParkingRentals() {
        when(parkingRentalRepository.findByNeighbourhoodIdAndStatus(NEIGHBOURHOOD_ID, ParkingRental.ParkingRentalStatus.AVAILABLE))
                .thenReturn(Arrays.asList(rental1, rental2));

        List<ParkingRental> availableRentals = parkingRentalService.getAvailableParkingRentals(NEIGHBOURHOOD_ID);

        assertNotNull(availableRentals);
        assertEquals(2, availableRentals.size());
        assertEquals(SPOT_10, availableRentals.get(0).getSpot());
        assertEquals(SPOT_20, availableRentals.get(1).getSpot());

        verify(parkingRentalRepository, times(1))
                .findByNeighbourhoodIdAndStatus(NEIGHBOURHOOD_ID, ParkingRental.ParkingRentalStatus.AVAILABLE);
    }

    @Test
    void testCreateParkingRental() {
        ParkingRental savedRental = ParkingRental.builder()
                .rentalId(RENTAL_ID_3)
                .neighbourhoodId(rentalDTO.getNeighbourhoodId())
                .userId(rentalDTO.getUserId())
                .spot(rentalDTO.getSpot())
                .startTime(rentalDTO.getStartTime())
                .endTime(rentalDTO.getEndTime())
                .price(rentalDTO.getPrice())
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        when(parkingRentalRepository.save(any(ParkingRental.class))).thenReturn(savedRental);

        ParkingRental result = parkingRentalService.createParkingRental(rentalDTO);

        assertNotNull(result);
        assertEquals(RENTAL_ID_3, result.getRentalId());
        assertEquals(SPOT_30, result.getSpot());
        assertEquals(PRICE_25, result.getPrice());

        verify(parkingRentalRepository, times(1)).save(any(ParkingRental.class));
    }
}
