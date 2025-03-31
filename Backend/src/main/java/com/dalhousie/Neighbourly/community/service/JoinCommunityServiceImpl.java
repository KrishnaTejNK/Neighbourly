package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.helprequest.service.HelpRequestService;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.util.CustomResponseBody;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of JoinCommunityService for handling community join requests.
 */
@Service
@RequiredArgsConstructor
public class JoinCommunityServiceImpl implements JoinCommunityService {

    private final HelpRequestService helpRequestService;
    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;

    @Override
    public CommunityResponse storeJoinRequest(HelpRequestDTO dto) {
        return helpRequestService.storeJoinRequest(dto);
    }

    @Override
    @Transactional
    public CustomResponseBody<CommunityResponse> approveJoinRequest(int requestId) {
        Optional<HelpRequest> requestOptional = fetchRequestById(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Join request not found");
        }

        HelpRequest request = requestOptional.get();
        Optional<User> userOptional = fetchUserById(request.getUser().getId());
        if (userOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
        }

        User user = userOptional.get();
        updateUserDetails(user, request);

        // Change the status of the request to APPROVED
        request.setStatus(RequestStatus.APPROVED);
        helpRequestRepository.save(request);

        // Create response
        CommunityResponse response = new CommunityResponse(user.getId(), user.getNeighbourhood_id(), RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "User approved and added as a resident with contact and address.");
    }

    @Override
    @Transactional
    public CustomResponseBody<CommunityResponse> denyJoinRequest(int requestId) {
        Optional<HelpRequest> requestOptional = fetchRequestById(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Join request not found");
        }

        HelpRequest request = requestOptional.get();
        // Change the status of the request to DECLINED
        request.setStatus(RequestStatus.DECLINED);
        helpRequestRepository.save(request);

        // Create the response
        CommunityResponse response = new CommunityResponse(request.getUser().getId(), request.getNeighbourhood().getNeighbourhoodId(), RequestStatus.DECLINED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "User denied and request status updated");
    }

    // Helper method to fetch request by ID
    private Optional<HelpRequest> fetchRequestById(int requestId) {
        return helpRequestRepository.findById(requestId);
    }

    // Helper method to fetch user by ID
    private Optional<User> fetchUserById(int userId) {
        return userRepository.findById(userId);
    }

    // Helper method to update user details from request
    private void updateUserDetails(User user, HelpRequest request) {
        String description = request.getDescription();
        String phone = extractPhone(description);
        String address = extractAddress(description);

        user.setUserType(UserType.RESIDENT);
        user.setNeighbourhood_id(request.getNeighbourhood().getNeighbourhoodId());

        if (phone != null) user.setContact(phone);
        if (address != null) user.setAddress(address);

        userRepository.save(user);
    }

    // Helper method to extract phone from description
    private String extractPhone(String description) {
        if (description.contains("Phone: ")) {
            return description.split("Phone: ")[1].split(",")[0].trim();
        }
        return null;
    }

    // Helper method to extract address from description
    private String extractAddress(String description) {
        if (description.contains("Address: ")) {
            return description.split("Address: ")[1].trim();
        }
        return null;
    }
}