package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
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
            return handleRequestNotFound(requestId);
        }

        HelpRequest request = requestOptional.get();
        if (isNotCreateRequest(request, requestId)) return handleInvalidRequestType(requestId);

        Optional<User> userOptional = userRepository.findById(request.getUser().getId());
        if (userOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
        }

        User user = userOptional.get();
        String[] details = extractDetailsFromDescription(request.getDescription());
        String location = details[0], phone = details[1], address = details[2];

        Neighbourhood savedNeighbourhood = createNewNeighbourhood(location,address);

        updateUserAndSave(user, savedNeighbourhood, phone, address);

        updateRequestStatusToApproved(request);

        CommunityResponse response = new CommunityResponse(user.getId(), savedNeighbourhood.getNeighbourhoodId(), RequestStatus.APPROVED);
        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community successfully created");
    }

    private String[] extractDetailsFromDescription(String description) {
        String location = extractDetail(description, "location: ", " | Phone:");
        String phone = extractDetail(description, "Phone: ", " | Address:");
        String address = extractDetail(description, "Address: ", null);
        return new String[]{location, phone, address};
    }
    private String extractDetail(String description, String startDelimiter, String endDelimiter) {
        int start = description.indexOf(startDelimiter) + startDelimiter.length();
        if (start == -1) return null;

        if (endDelimiter == null) {
            return description.substring(start).trim();
        }

        int end = description.indexOf(endDelimiter, start);
        return end == -1 ? description.substring(start).trim() : description.substring(start, end).trim();
    }

    private Neighbourhood createNewNeighbourhood( String location,String address) {
        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setLocation(address);
        neighbourhood.setName(location);
        return neighbourhoodRepository.save(neighbourhood);
    }

    private void updateUserAndSave(User user, Neighbourhood savedNeighbourhood, String phone, String address) {
        user.setUserType(UserType.COMMUNITY_MANAGER);
        user.setNeighbourhood_id(savedNeighbourhood.getNeighbourhoodId());
        user.setContact(phone);
        user.setAddress(address);
        userRepository.save(user);
    }

    private void updateRequestStatusToApproved(HelpRequest request) {
        request.setStatus(RequestStatus.APPROVED);
        helpRequestRepository.save(request);
    }

    private boolean isNotCreateRequest(HelpRequest request, int requestId) {
        if (request.getRequestType() != HelpRequest.RequestType.CREATE) {
            log.error("Request ID {} is not a CREATE request, but a {}", requestId, request.getRequestType());
            return true;
        }
        return false;
    }

    private CustomResponseBody<CommunityResponse> handleRequestNotFound(int requestId) {
        log.error("Create request with ID {} not found in the database", requestId);
        return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Create request not found");
    }

    private CustomResponseBody<CommunityResponse> handleInvalidRequestType(int requestId) {
        log.error("Request ID {} is not a CREATE request", requestId);
        return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Invalid request type");
    }



    @Transactional
    public CustomResponseBody<CommunityResponse> denyCreateRequest(int requestId) {
        Optional<HelpRequest> requestOptional = helpRequestRepository.findByRequestId(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Create request not found");
        }

        HelpRequest request = requestOptional.get();
        request.setStatus(RequestStatus.DECLINED);
        helpRequestRepository.save(request);

        CommunityResponse response = new CommunityResponse(request.getUser().getId(), 0, RequestStatus.DECLINED);
        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "Community creation request denied");
    }
}
