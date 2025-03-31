package com.dalhousie.Neighbourly.helprequest.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;

import java.util.List;

/**
 * Service interface for managing help requests related to community joining and creation.
 */
public interface HelpRequestService {

    /**
     * Stores a request to join a neighbourhood community.
     * @param dto Data transfer object containing request details
     * @return CommunityResponse with the result of the join request
     */
    CommunityResponse storeJoinRequest(HelpRequestDTO dto);

    /**
     * Stores a request to create a new community.
     * @param dto Data transfer object containing request details
     * @return CommunityResponse with the result of the create request
     */
    CommunityResponse storeCreateRequest(HelpRequestDTO dto);

    /**
     * Retrieves all open join requests for a specific neighbourhood.
     * @param neighbourhoodId The ID of the neighbourhood
     * @return List of HelpRequest objects representing join requests
     */
    List<HelpRequest> getAllJoinCommunityRequests(int neighbourhoodId);

    /**
     * Retrieves all open community creation requests.
     * @return List of HelpRequestDTO objects representing open create requests
     */
    List<HelpRequestDTO> getAllOpenCommunityRequests();
}