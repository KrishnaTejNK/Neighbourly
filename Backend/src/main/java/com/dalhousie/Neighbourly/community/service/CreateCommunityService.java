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

        // Extract details from description
        String description = request.getDescription();
        String location = extractDetail(description, "location: ", " | Phone:");
        String phone = extractDetail(description, "Phone: ", " | Address:");
        String address = extractDetail(description, "Address: ", null); // Last field, no end delimiter

        log.info("Extracted location: {}, phone: {}, address: {}", location, phone, address);

        // Create new neighborhood
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setLocation(location);
        neighbourhood.setName(location);
        Neighbourhood savedNeighbourhood = neighbourhoodRepository.save(neighbourhood);

        // Update user details
        user.setUserType(UserType.COMMUNITY_MANAGER);
        user.setNeighbourhood_id(savedNeighbourhood.getNeighbourhoodId());
        user.setContact(phone);
        user.setAddress(address);
        userRepository.save(user);

        // Update request status
        request.setStatus(HelpRequest.RequestStatus.APPROVED);
        helpRequestRepository.save(request);

        // Create response
        CommunityResponse response = new CommunityResponse(user.getId(), savedNeighbourhood.getNeighbourhoodId(), HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community successfully created");
    }
    private String extractDetail(String description, String startDelimiter, String endDelimiter) {
        int start = description.indexOf(startDelimiter) + startDelimiter.length();
        if (start == -1) return null; // Return null if not found

        if (endDelimiter == null) {
            return description.substring(start).trim(); // If no end delimiter, return rest of string
        }

        int end = description.indexOf(endDelimiter, start);
        return end == -1 ? description.substring(start).trim() : description.substring(start, end).trim();
    }


    @Transactional
    public CustomResponseBody<CommunityResponse> denyCreateRequest(int requestId) {
        // Fetch request details
        Optional<HelpRequest> requestOptional = helpRequestRepository.findByRequestId(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Create request not found");
        }

        HelpRequest request = requestOptional.get();

        // Update request status to DECLINED
        request.setStatus(HelpRequest.RequestStatus.DECLINED);
        helpRequestRepository.save(request);
        int initialNeighbourhoodId = 0;
        // Create response
        CommunityResponse response = new CommunityResponse(request.getUser().getId(), initialNeighbourhoodId, HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community creation request denied");
    }
}
