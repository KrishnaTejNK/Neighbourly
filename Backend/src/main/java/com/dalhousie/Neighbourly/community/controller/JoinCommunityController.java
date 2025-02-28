package com.dalhousie.Neighbourly.community.controller;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.community.service.JoinCommunityService;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.community.dto.JoinCommunityDTO;
import com.dalhousie.Neighbourly.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/join-community")
@RequiredArgsConstructor
public class JoinCommunityController {

    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final JoinCommunityService joinCommunityService;

    @PostMapping("/join")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> joinCommunity(@RequestBody JoinCommunityDTO joinRequest) {

        // Fetch userId using email
        Optional<User> userOptional = userRepository.findByEmail(joinRequest.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found"));
        }

        User user = userOptional.get();

        // Fetch neighbourhoodId using pincode (location field)
        Optional<Neighbourhood> neighbourhoodOptional = neighbourhoodRepository.findByLocation(joinRequest.getPincode());
        if (neighbourhoodOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "No neighbourhood found for the given pincode"));
        }

        Neighbourhood neighbourhood = neighbourhoodOptional.get();

        // Create a help request entry
        HelpRequestDTO helpRequestDTO = new HelpRequestDTO();
        helpRequestDTO.setUserId(user.getId());  // Set fetched userId
        helpRequestDTO.setNeighbourhoodId(neighbourhood.getNeighbourhoodId());  // Set fetched neighbourhoodId
        helpRequestDTO.setRequestType("JOIN_COMMUNITY");
        helpRequestDTO.setDescription(user.getName() + " has requested to join the community.");

        // Call service to handle request creation
        CommunityResponse joinCommunityResponse = joinCommunityService.storeJoinRequest(helpRequestDTO);

        return ResponseEntity.ok(new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, joinCommunityResponse, "User request submitted successfully"));
    }

    @PostMapping("/approve-join/{requestId}")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> approveJoinRequest(@PathVariable int requestId) {
        CustomResponseBody<CommunityResponse> response = joinCommunityService.approveJoinRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deny-join/{requestId}")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> denyRequest(@PathVariable int requestId) {
        CustomResponseBody<CommunityResponse> response = joinCommunityService.denyJoinRequest(requestId);
        return ResponseEntity.ok(response);
    }

}
