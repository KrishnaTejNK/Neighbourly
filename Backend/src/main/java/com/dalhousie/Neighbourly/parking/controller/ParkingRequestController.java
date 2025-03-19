package com.dalhousie.Neighbourly.parking.controller;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.service.ParkingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/requests")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend calls
public class ParkingRequestController {
    @Autowired
    private ParkingRequestService parkingRequestService;

    @PostMapping
    public ResponseEntity<String> createParkingRequest(@RequestBody ParkingRequestDTO parkingRequestDTO) {
        parkingRequestService.createParkingRequest(parkingRequestDTO);
        return ResponseEntity.ok("Parking request created successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ParkingResponseDTO>> getParkingRequests(@PathVariable int userId) {
        return ResponseEntity.ok(parkingRequestService.getParkingRequestsForOwner(userId));
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable int requestId) {
        parkingRequestService.approveRequest(requestId);
        return ResponseEntity.ok("Parking request approved.");
    }

    @PutMapping("/{requestId}/deny")
    public ResponseEntity<String> denyRequest(@PathVariable int requestId) {
        parkingRequestService.denyRequest(requestId);
        return ResponseEntity.ok("Parking request denied.");
    }
}
