package com.dalhousie.Neighbourly.amenity.repository;


import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    List<Amenity> findByNeighbourhoodId(int neighbourhoodId);
}
