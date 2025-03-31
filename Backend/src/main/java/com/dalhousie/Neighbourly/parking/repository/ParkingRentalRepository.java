package com.dalhousie.Neighbourly.parking.repository;

import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingRentalRepository extends JpaRepository<ParkingRental, Integer> {
    List<ParkingRental> findByNeighbourhoodId(int neighbourhoodId);
    List<ParkingRental> findByNeighbourhoodIdAndStatus(int neighbourhoodId, ParkingRentalStatus status);
    List<ParkingRental> findByUserId(int ownerId);

}
