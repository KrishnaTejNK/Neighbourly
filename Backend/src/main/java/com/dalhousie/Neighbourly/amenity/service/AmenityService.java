package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AmenityService {

    @Autowired
    private AmenityRepository amenityRepository;

    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public List<Amenity> getAmenitiesByNeighbourhood(int neighbourhoodId) {
        return amenityRepository.findByNeighbourhoodId(neighbourhoodId);
    }

    public Amenity createAmenity(Amenity amenity) {
        return amenityRepository.save(amenity);
    }

    public void deleteAmenity(int amenityId) {
        amenityRepository.deleteById(amenityId);
    }


}


