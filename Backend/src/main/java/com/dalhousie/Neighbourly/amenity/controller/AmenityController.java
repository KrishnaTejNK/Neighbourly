package com.dalhousie.Neighbourly.amenity.controller;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/amenities")
@CrossOrigin(origins = "http://172.17.2.103:3080/")
public class AmenityController {

    private final AmenityService amenityService;

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
