package com.dalhousie.Neighbourly.neighbourhood.repository;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeighbourhoodRepository extends JpaRepository<Neighbourhood, Integer> {
    Optional<Neighbourhood> findByLocation(String location);
    Optional<Neighbourhood> findByNeighbourhoodId(int neighbourhoodId);

}

