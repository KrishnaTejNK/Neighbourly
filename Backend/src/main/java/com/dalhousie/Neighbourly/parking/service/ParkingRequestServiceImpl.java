package com.dalhousie.Neighbourly.parking.service;

import com.dalhousie.Neighbourly.parking.dto.ParkingRequestDTO;
import com.dalhousie.Neighbourly.parking.dto.ParkingResponseDTO;
import com.dalhousie.Neighbourly.parking.entity.ParkingRental;
import com.dalhousie.Neighbourly.parking.entity.ParkingRentalStatus;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequest;
import com.dalhousie.Neighbourly.parking.entity.ParkingRequestStatus;
import com.dalhousie.Neighbourly.parking.repository.ParkingRentalRepository;
import com.dalhousie.Neighbourly.parking.repository.ParkingRequestRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ParkingRequestService for handling parking request operations.
 */
@Service
@RequiredArgsConstructor
public class ParkingRequestServiceImpl implements ParkingRequestService {

    private final ParkingRequestRepository parkingRequestRepository;
    private final ParkingRentalRepository parkingRentalRepository;
    private final UserRepository userRepository;

    @Override
    public void createParkingRequest(ParkingRequestDTO parkingRequestDTO) {
        ParkingRental rental = parkingRentalRepository.findById(parkingRequestDTO.getRentalId())
                .orElseThrow(() -> new RuntimeException("Parking rental not found"));
        User user = userRepository.findById(parkingRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRequest request = new ParkingRequest();
        request.setParkingRental(rental);
        request.setUser(user);
        request.setStatus(ParkingRequestStatus.PENDING);

        parkingRequestRepository.save(request);
    }

    @Override
    public List<ParkingResponseDTO> getParkingRequestsForOwner(int ownerId) {
        // Step 1: Get all rental IDs owned by this user
        List<Integer> rentalIds = parkingRentalRepository.findByUserId(ownerId)
                .stream()
                .map(ParkingRental::getRentalId)
                .collect(Collectors.toList());

        if (rentalIds.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no rentals exist
        }

        // Step 2: Find all parking requests where rental_id is in the owner's rental list
        List<ParkingRequest> requests = parkingRequestRepository.findByParkingRental_RentalIdIn(rentalIds);

        // Step 3: Convert to DTO
        return requests.stream()
                .map(request -> new ParkingResponseDTO(
                        request.getRequestId(),
                        request.getParkingRental().getRentalId(),
                        request.getUser().getId(),
                        request.getStatus().name(),
                        request.getUser().getName(),
                        request.getParkingRental().getSpot()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void approveRequest(int requestId) {
        ParkingRequest request = parkingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(ParkingRequestStatus.APPROVED);
        parkingRequestRepository.save(request);

        // Update the rental status to BOOKED
        ParkingRental rental = request.getParkingRental();
        rental.setStatus(ParkingRentalStatus.BOOKED);
        parkingRentalRepository.save(rental);
    }

    @Override
    public void denyRequest(int requestId) {
        ParkingRequest request = parkingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(ParkingRequestStatus.DENIED);
        parkingRequestRepository.save(request);
    }
}