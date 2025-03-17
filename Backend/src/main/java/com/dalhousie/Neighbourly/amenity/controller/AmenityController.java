package com.dalhousie.Neighbourly.amenity.controller;


import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping("/{neighbourhoodId}")
    public List<Amenity> getAmenities(@PathVariable int neighbourhoodId) {
        return amenityService.getAmenitiesByNeighbourhood(neighbourhoodId);
    }

    @PostMapping
    public Amenity createAmenity(@RequestBody Amenity amenity) {
        return amenityService.createAmenity(amenity);
    }

    @DeleteMapping("/{amenityId}")
    public void deleteAmenity(@PathVariable int amenityId) {
        amenityService.deleteAmenity(amenityId);
    }

}
