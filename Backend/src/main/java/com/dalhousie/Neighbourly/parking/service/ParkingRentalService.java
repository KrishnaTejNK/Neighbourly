package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingRentalService {

    @Autowired
    private ParkingRentalRepository parkingRentalRepository;

    public List<ParkingRental> getAvailableParkingRentals(int neighbourhoodId) {
        return parkingRentalRepository.findByNeighbourhoodIdAndStatus(neighbourhoodId, ParkingRental.ParkingRentalStatus.AVAILABLE);
    }

    public ParkingRental createParkingRental(ParkingRentalDTO dto) {
        ParkingRental rental = ParkingRental.builder()
                .neighbourhoodId(dto.getNeighbourhoodId())
                .userId(dto.getUserId())
                .spot(dto.getSpot())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .price(dto.getPrice())
                .status(ParkingRental.ParkingRentalStatus.AVAILABLE)
                .build();

        return parkingRentalRepository.save(rental);
    }
}
