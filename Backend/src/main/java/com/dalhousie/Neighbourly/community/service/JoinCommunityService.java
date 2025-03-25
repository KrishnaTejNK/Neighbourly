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

        // Extract contact and address from description
        String description = request.getDescription();
        String phone = description.contains("Phone: ") ? description.split("Phone: ")[1].split(",")[0].trim() : null;
        String address = description.contains("Address: ") ? description.split("Address: ")[1].trim() : null;

        // Assign extracted details
        user.setUserType(UserType.RESIDENT);
        user.setNeighbourhood_id(request.getNeighbourhood().getNeighbourhoodId());

        if (phone != null) user.setContact(phone);
        if (address != null) user.setAddress(address);

        // Save updated user
        userRepository.save(user);

        // Change the status of the request to APPROVED
        request.setStatus(HelpRequest.RequestStatus.APPROVED);
        helpRequestRepository.save(request);

        // Create response
        CommunityResponse response = new CommunityResponse(user.getId(), user.getNeighbourhood_id(), HelpRequest.RequestStatus.APPROVED);

        return new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, response, "User approved and added as a resident with contact and address.");
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





