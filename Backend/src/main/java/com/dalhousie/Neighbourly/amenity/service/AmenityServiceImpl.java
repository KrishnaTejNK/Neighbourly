package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    @Override
    public List<Amenity> getAmenitiesByNeighbourhood(int neighbourhoodId) {
        return amenityRepository.findByNeighbourhoodId(neighbourhoodId);
    }

    @Override
    public Amenity createAmenity(Amenity amenity) {
        return amenityRepository.save(amenity);
    }

    @Override
    public void deleteAmenity(int amenityId) {
        amenityRepository.deleteById(amenityId);
    }
}



