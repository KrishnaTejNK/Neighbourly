
package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;

import java.util.List;

public interface AmenityService {
    List<Amenity> getAmenitiesByNeighbourhood(int neighbourhoodId);
    Amenity createAmenity(Amenity amenity);
    void deleteAmenity(int amenityId);
}
