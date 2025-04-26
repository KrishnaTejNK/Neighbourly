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
        Optional<User> userOptional = userRepository.findByEmail(createRequest.getEmail());
        if (userOptional.isEmpty()) {
            CustomResponseBody<CommunityResponse> errorResponse =
                    new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        User user = userOptional.get();
        String description = buildDescription(user, createRequest);
        HelpRequestDTO helpRequestDTO = buildHelpRequest(user, description);

        CommunityResponse response = createCommunityService.storeCreateRequest(helpRequestDTO);
        CustomResponseBody<CommunityResponse> successResponse =
                new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community creation request submitted successfully");

        return ResponseEntity.ok(successResponse);
    }

    private String buildDescription(User user, CreateCommunityDTO createRequest) {
        return String.format(
                "User %s requested to create a community at location: %s | Phone: %s | Address: %s",
                user.getName(),
                createRequest.getAddress(),
                createRequest.getPhone(),
                createRequest.getPincode()
        );
    }

    private HelpRequestDTO buildHelpRequest(User user, String description) {
        HelpRequestDTO helpRequestDTO = new HelpRequestDTO();
        helpRequestDTO.setUserId(user.getId());
        helpRequestDTO.setRequestType("CREATE_COMMUNITY");
        helpRequestDTO.setDescription(description);
        return helpRequestDTO;
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