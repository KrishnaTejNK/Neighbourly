package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRentalStatus;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ParkingRentalService for handling parking rental operations.
 */
@Service
@RequiredArgsConstructor
public class ParkingRentalServiceImpl implements ParkingRentalService {

    private final ParkingRentalRepository parkingRentalRepository;

    @Override
    public List<ParkingRental> getAvailableParkingRentals(int neighbourhoodId) {
        return getParkingRentalsByStatus(neighbourhoodId, ParkingRentalStatus.AVAILABLE);
    }

    private List<ParkingRental> getParkingRentalsByStatus(int neighbourhoodId, ParkingRentalStatus status) {
        return parkingRentalRepository.findByNeighbourhoodIdAndStatus(neighbourhoodId, status);
    }

    @Override
    public ParkingRental createParkingRental(ParkingRentalDTO dto) {
        ParkingRental rental = buildParkingRental(dto);
        return parkingRentalRepository.save(rental);
    }

    private ParkingRental buildParkingRental(ParkingRentalDTO dto) {
        return ParkingRental.builder()
                .neighbourhoodId(dto.getNeighbourhoodId())
                .userId(dto.getUserId())
                .spot(dto.getSpot())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .price(dto.getPrice())
                .status(ParkingRentalStatus.AVAILABLE)
                .build();
    }
}