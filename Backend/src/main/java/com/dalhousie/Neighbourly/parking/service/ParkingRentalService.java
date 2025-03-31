package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRentalDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;

import java.util.List;

/**
 * Service interface for managing parking rental operations.
 */
public interface ParkingRentalService {

    /**
     * Retrieves all available parking rentals for a given neighbourhood.
     * @param neighbourhoodId The ID of the neighbourhood
     * @return List of ParkingRental objects representing available rentals
     */
    List<ParkingRental> getAvailableParkingRentals(int neighbourhoodId);

    /**
     * Creates a new parking rental based on the provided details.
     * @param dto Data transfer object containing parking rental details
     * @return The created ParkingRental object
     */
    ParkingRental createParkingRental(ParkingRentalDTO dto);
}