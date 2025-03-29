package com.dalhousie.Neighbourly.parking.repository;

import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingRentalRepository extends JpaRepository<ParkingRental, Integer> {
    List<ParkingRental> findByNeighbourhoodId(int neighbourhoodId);
    List<ParkingRental> findByNeighbourhoodIdAndStatus(int neighbourhoodId, ParkingRental.ParkingRentalStatus status);
    List<ParkingRental> findByUserId(int ownerId);

}
