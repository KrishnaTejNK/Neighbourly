package com.dalhousie.Neighbourly.parking.controller;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.service.ParkingRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing parking rental operations.
 */
@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "http://localhost:3000") // Adjust based on frontend URL
@RequiredArgsConstructor
public class ParkingRentalController {

    private final ParkingRentalService parkingRentalService;

    /**
     * Retrieves all available parking rentals for a given neighbourhood.
     * @param neighbourhoodId The ID of the neighbourhood
     * @return List of available ParkingRental objects
     */
    @GetMapping("/{neighbourhoodId}")
    public List<ParkingRental> getAvailableParking(@PathVariable int neighbourhoodId) {
        return parkingRentalService.getAvailableParkingRentals(neighbourhoodId);
    }

    /**
     * Creates a new parking rental.
     * @param dto Data transfer object containing parking rental details
     * @return The created ParkingRental object
     */
    @PostMapping("/create")
    public ParkingRental createParkingRental(@RequestBody ParkingRentalDTO dto) {
        return parkingRentalService.createParkingRental(dto);
    }
}
