package com.dalhousie.Neighbourly.parking.repository;

import com.dalhousie.Neighbourly.parking.entity.ParkingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRequestRepository extends JpaRepository<ParkingRequest, Integer> {

    List<ParkingRequest> findByParkingRental_RentalIdIn(List<Integer> rentalIds);

}
