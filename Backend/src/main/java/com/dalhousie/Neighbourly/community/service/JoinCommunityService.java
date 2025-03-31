package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.util.CustomResponseBody;

/**
 * Service interface for managing community join requests.
 */
public interface JoinCommunityService {

    /**
     * Stores a request to join a community.
     * @param dto Data transfer object containing request details
     * @return CommunityResponse with the result of the join request
     */
    CommunityResponse storeJoinRequest(HelpRequestDTO dto);

    /**
     * Approves a community join request and updates user details.
     * @param requestId The ID of the request to approve
     * @return CustomResponseBody containing the result of the approval
     */
    CustomResponseBody<CommunityResponse> approveJoinRequest(int requestId);

    /**
     * Denies a community join request.
     * @param requestId The ID of the request to deny
     * @return CustomResponseBody containing the result of the denial
     */
    CustomResponseBody<CommunityResponse> denyJoinRequest(int requestId);
}