package com.dalhousie.Neighbourly.parking.controller;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.service.ParkingRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "http://localhost:3000") // Adjust based on frontend URL
public class ParkingRentalController {

    @Autowired
    private ParkingRentalService parkingRentalService;

    @GetMapping("/{neighbourhoodId}")
    public List<ParkingRental> getAvailableParking(@PathVariable int neighbourhoodId) {
        return parkingRentalService.getAvailableParkingRentals(neighbourhoodId);
    }

    @PostMapping("/create")
    public ParkingRental createParkingRental(@RequestBody ParkingRentalDTO dto) {
        return parkingRentalService.createParkingRental(dto);
    }
}
