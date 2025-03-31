package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;

import java.util.List;

/**
 * Service interface for managing parking request operations.
 */
public interface ParkingRequestService {

    /**
     * Creates a new parking request based on the provided details.
     * @param parkingRequestDTO Data transfer object containing parking request details
     * @throws RuntimeException if the rental or user is not found
     */
    void createParkingRequest(ParkingRequestDTO parkingRequestDTO);

    /**
     * Retrieves all parking requests for a specific owner.
     * @param ownerId The ID of the owner (user who owns the rentals)
     * @return List of ParkingResponseDTO objects representing the requests
     */
    List<ParkingResponseDTO> getParkingRequestsForOwner(int ownerId);

    /**
     * Approves a parking request and updates the associated rental status.
     * @param requestId The ID of the request to approve
     * @throws RuntimeException if the request is not found
     */
    void approveRequest(int requestId);

    /**
     * Denies a parking request.
     * @param requestId The ID of the request to deny
     * @throws RuntimeException if the request is not found
     */
    void denyRequest(int requestId);
}