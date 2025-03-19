package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequest;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import com.dalhousie.Neighbourly.parking.repository.ParkingRequestRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingRequestService {
    @Autowired
    private ParkingRequestRepository parkingRequestRepository;
    @Autowired
    private ParkingRentalRepository parkingRentalRepository;
    @Autowired
    private UserRepository userRepository;

    public void createParkingRequest(ParkingRequestDTO ParkingRequestDTO) {
        ParkingRental rental = parkingRentalRepository.findById(ParkingRequestDTO.getRentalId())
                .orElseThrow(() -> new RuntimeException("Parking rental not found"));
        User user = userRepository.findById(ParkingRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRequest request = new ParkingRequest();
        request.setParkingRental(rental);
        request.setUser(user);
        request.setStatus(ParkingRequest.ParkingRequestStatus.PENDING);
        parkingRequestRepository.save(request);
    }

    public List<ParkingResponseDTO> getParkingRequestsForOwner(int ownerId) {
        // Step 1: Get all rental IDs owned by this user
        List<Integer> rentalIds = parkingRentalRepository.findByUserId(ownerId)
                .stream()
                .map(ParkingRental::getRentalId)
                .collect(Collectors.toList());

        if (rentalIds.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no rentals exist
        }
        System.out.println("Hi");
        System.out.println(rentalIds);
        // Step 2: Find all parking requests where rental_id is in the owner's rental list
        List<ParkingRequest> requests = parkingRequestRepository.findByParkingRental_RentalIdIn(rentalIds);

        // Step 3: Convert to DTO
        return requests.stream()
                .map(request -> new ParkingResponseDTO(
                        request.getRequestId(),
                        request.getParkingRental().getRentalId(),
                        request.getUser().getId(),
                        request.getStatus().name()))
                .collect(Collectors.toList());
    }


    public void approveRequest(int requestId) {
        ParkingRequest request = parkingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(ParkingRequest.ParkingRequestStatus.APPROVED);
        parkingRequestRepository.save(request);

        // Update the rental status to BOOKED
        ParkingRental rental = request.getParkingRental();
        rental.setStatus(ParkingRental.ParkingRentalStatus.BOOKED);
        parkingRentalRepository.save(rental);
    }

    public void denyRequest(int requestId) {
        ParkingRequest request = parkingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(ParkingRequest.ParkingRequestStatus.DENIED);
        parkingRequestRepository.save(request);
    }
}
