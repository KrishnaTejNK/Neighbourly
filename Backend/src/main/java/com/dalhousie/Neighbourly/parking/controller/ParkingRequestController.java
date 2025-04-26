package com.dalhousie.Neighbourly.parking.controller;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.service.ParkingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing parking request operations.
 */
@RestController
@RequestMapping("/api/parking/requests")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend calls
@RequiredArgsConstructor
public class ParkingRequestController {
    private static final int SUCCESS_STATUS = 201;
    private final ParkingRequestService parkingRequestService;

    /**
     * Creates a new parking request.
     * @param parkingRequestDTO Data transfer object containing parking request details
     * @return ResponseEntity with a success message
     */
    @PostMapping
    public ResponseEntity<String> createParkingRequest(@RequestBody ParkingRequestDTO parkingRequestDTO) {

        parkingRequestService.createParkingRequest(parkingRequestDTO);
        return ResponseEntity.status(SUCCESS_STATUS).body("Parking request created successfully.");
    }

    /**
     * Retrieves all parking requests for a specific owner.
     * @param userId The ID of the owner (user who owns the rentals)
     * @return ResponseEntity with a list of ParkingResponseDTO objects
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ParkingResponseDTO>> getParkingRequests(@PathVariable int userId) {
        List<ParkingResponseDTO> requests = parkingRequestService.getParkingRequestsForOwner(userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Approves a parking request.
     * @param requestId The ID of the request to approve
     * @return ResponseEntity with a success message
     */
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable int requestId) {
        parkingRequestService.approveRequest(requestId);
        return ResponseEntity.ok("Parking request approved.");
    }

    /**
     * Denies a parking request.
     * @param requestId The ID of the request to deny
     * @return ResponseEntity with a success message
     */
    @PutMapping("/{requestId}/deny")
    public ResponseEntity<String> denyRequest(@PathVariable int requestId) {
        parkingRequestService.denyRequest(requestId);
        return ResponseEntity.ok("Parking request denied.");
    }
}