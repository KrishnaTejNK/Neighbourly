package com.dalhousie.Neighbourly.booking.repository;

import com.dalhousie.Neighbourly.booking.entity.BookingRequest;
import com.dalhousie.Neighbourly.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, Integer> {

    @Query("SELECT b FROM BookingRequest b WHERE b.neighbourhood_id = :neighbourhoodId")
    List<BookingRequest> findByNeighbourhood_id(int neighbourhoodId);


    @Query("SELECT b FROM BookingRequest b WHERE b.amenity_id = :amenityId ")
    List<BookingRequest> findByAmenity_id(int amenityId);  // NEW METHOD

    @Query("SELECT b FROM BookingRequest b WHERE b.neighbourhood_id = :neighbourhoodId and b.status = :status")
    List<BookingRequest> findByNeighbourhood_idAndStatus(int neighbourhoodId, BookingStatus status);


}
