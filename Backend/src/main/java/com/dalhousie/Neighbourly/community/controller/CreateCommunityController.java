package com.dalhousie.Neighbourly.community.controller;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.community.dto.CreateCommunityDTO;
import com.dalhousie.Neighbourly.community.service.CreateCommunityService;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.util.CustomResponseBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/create-community")
@RequiredArgsConstructor
public class CreateCommunityController {

    private final UserRepository userRepository;
    private final CreateCommunityService createCommunityService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> requestCommunityCreation(@RequestBody CreateCommunityDTO createRequest) {

        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(createRequest.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found"));
        }
        User user = userOptional.get();

        // Prepare HelpRequestDTO
        HelpRequestDTO helpRequestDTO = new HelpRequestDTO();
        helpRequestDTO.setUserId(user.getId());
        helpRequestDTO.setRequestType("CREATE_COMMUNITY");
        helpRequestDTO.setDescription("User " + user.getName() + " requested to create community at location: " + createRequest.getAddress() + " with pincode: " + createRequest.getPincode());

        // Call service to create help request
        CommunityResponse response = createCommunityService.storeCreateRequest(helpRequestDTO);

        return ResponseEntity.ok(new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community creation request submitted successfully"));
    }

    @PostMapping("/approve-create/{requestId}")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> approveCreateRequest(@PathVariable int requestId) {
        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deny-create/{requestId}")
    public ResponseEntity<CustomResponseBody<CommunityResponse>> denyCreateRequest(@PathVariable int requestId) {
        CustomResponseBody<CommunityResponse> response = createCommunityService.denyCreateRequest(requestId);
        return ResponseEntity.ok(response);
    }
}
