package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.helprequest.service.HelpRequestService;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.util.CustomResponseBody;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCommunityService {
    private final HelpRequestService helpRequestService;
    private final HelpRequestRepository helpRequestRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final UserRepository userRepository;


    public CommunityResponse storeCreateRequest(HelpRequestDTO dto) {
        return helpRequestService.storeCreateRequest(dto);
    }


    @Transactional
    public CustomResponseBody<CommunityResponse> approveCreateRequest(int requestId) {

        Optional<HelpRequest> requestOptional = helpRequestRepository.findByRequestId(requestId);


        if (requestOptional.isEmpty()) {
            log.error("Create request with ID {} not found in the database", requestId);
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Create request not found");
        }

        HelpRequest request = requestOptional.get();

        // Verify request type
        if (request.getRequestType() != HelpRequest.RequestType.CREATE) {
            log.error("Request ID {} is not a CREATE request, but a {}", requestId, request.getRequestType());
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Invalid request type");
        }




        // Fetch user details
        Optional<User> userOptional = userRepository.findById(request.getUser().getId());
        if (userOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
        }

        User user = userOptional.get();

// Extract the pincode from the description
        String description = request.getDescription();
        String pincode = description.substring(description.lastIndexOf("pincode: ") + 9).trim();

// Extract location part from the description
        String locationPart = description.split("location: ")[1].split(" with pincode")[0].trim();

// Create new neighbourhood with the extracted pincode
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setLocation(pincode);
        neighbourhood.setName(locationPart);
        Neighbourhood savedNeighbourhood = neighbourhoodRepository.save(neighbourhood);


        // Assign user as community manager
        user.setUserType(UserType.COMMUNITY_MANAGER);
        user.setNeighbourhood_id(savedNeighbourhood.getNeighbourhoodId());
        userRepository.save(user);

        // Update request status to APPROVED
        request.setStatus(HelpRequest.RequestStatus.APPROVED);
        helpRequestRepository.save(request);

        // Create response
        CommunityResponse response = new CommunityResponse(request.getUser().getId(), neighbourhood.getNeighbourhoodId(), HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community successfully created");
    }

    @Transactional
    public CustomResponseBody<CommunityResponse> denyCreateRequest(int requestId) {
        // Fetch request details
        Optional<HelpRequest> requestOptional = helpRequestRepository.findByRequestId(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Create request not found");
        }

        HelpRequest request = requestOptional.get();

// Extract the pincode from the description
        String description = request.getDescription();
        String pincode = description.substring(description.lastIndexOf("pincode: ") + 9).trim();

// Extract location part from the description
        String locationPart = description.split("location: ")[1].split(" with pincode")[0].trim();
        // Update request status to DECLINED
        request.setStatus(HelpRequest.RequestStatus.DECLINED);
        helpRequestRepository.save(request);

        // Create response
        CommunityResponse response = new CommunityResponse(request.getUser().getId(), 0, HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community creation request denied");
    }
}
