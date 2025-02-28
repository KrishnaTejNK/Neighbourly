package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
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

@Service
@RequiredArgsConstructor
public class JoinCommunityService {
    private final HelpRequestService helpRequestService;
    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;

    public CommunityResponse storeJoinRequest(HelpRequestDTO dto) {
        return helpRequestService.storeJoinRequest(dto);
    }

    @Transactional
    public CustomResponseBody<CommunityResponse> approveJoinRequest(int requestId) {
        // Fetch help request details
        Optional<HelpRequest> requestOptional = helpRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Join request not found");
        }

        HelpRequest request = requestOptional.get();

        // Fetch user details
        Optional<User> userOptional = userRepository.findById(request.getUser().getId());
        if (userOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "User not found");
        }

        User user = userOptional.get();

        // Assign neighbourhood & update role
        user.setUserType(UserType.RESIDENT);
        user.setNeighbourhood_id(request.getNeighbourhood().getNeighbourhoodId());

        // Save updated user
        userRepository.save(user);

        // Change the status of the request to APPROVED instead of deleting it
        request.setStatus(HelpRequest.RequestStatus.APPROVED);  // Set status to APPROVED
        helpRequestRepository.save(request);  // Save the updated request

        // Create response
        CommunityResponse response = new CommunityResponse(user.getId(), user.getNeighbourhood_id(), HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "User approved and added as a resident");
    }

    @Transactional
    public CustomResponseBody<CommunityResponse> denyJoinRequest(int requestId) {
        // Fetch the help request details
        Optional<HelpRequest> requestOptional = helpRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            return new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Join request not found");
        }

        HelpRequest request = requestOptional.get();

        // Change the status of the request to DECLINED instead of deleting it
        request.setStatus(HelpRequest.RequestStatus.DECLINED);  // Set status to DECLINED
        helpRequestRepository.save(request);  // Save the updated request

        // Create the response
        CommunityResponse response = new CommunityResponse(request.getUser().getId(), request.getNeighbourhood().getNeighbourhoodId(), HelpRequest.RequestStatus.DECLINED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "User denied and request status updated");
    }


}





