package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.util.CustomResponseBody;

/**
 * Service interface for managing community creation requests.
 */
public interface CreateCommunityService {

    /**
     * Stores a request to create a new community.
     * @param dto Data transfer object containing request details
     * @return CommunityResponse with the result of the create request
     */
    CommunityResponse storeCreateRequest(HelpRequestDTO dto);

    /**
     * Approves a community creation request and creates the community.
     * @param requestId The ID of the request to approve
     * @return CustomResponseBody containing the result of the approval
     */
    CustomResponseBody<CommunityResponse> approveCreateRequest(int requestId);

    /**
     * Denies a community creation request.
     * @param requestId The ID of the request to deny
     * @return CustomResponseBody containing the result of the denial
     */
    CustomResponseBody<CommunityResponse> denyCreateRequest(int requestId);
}