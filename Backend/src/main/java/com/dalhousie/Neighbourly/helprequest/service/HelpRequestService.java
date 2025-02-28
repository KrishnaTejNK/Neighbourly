package com.dalhousie.Neighbourly.helprequest.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;

    public CommunityResponse storeJoinRequest(HelpRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Neighbourhood neighbourhood = neighbourhoodRepository.findById(dto.getNeighbourhoodId())
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found"));

        // Create the help request
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setUser(user);
        helpRequest.setNeighbourhood(neighbourhood);
        helpRequest.setRequestType(HelpRequest.RequestType.JOIN);
        helpRequest.setDescription(dto.getDescription());
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        helpRequest.setCreatedAt(LocalDateTime.now());

        // Only set neighbourhood if it's a JOIN request
        if (neighbourhood != null) {
            helpRequest.setNeighbourhood(neighbourhood);
        }

        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);

        // Convert HelpRequest to CommunityResponse before returning
        return new CommunityResponse(savedRequest.getUser().getId(), savedRequest.getNeighbourhood().getNeighbourhoodId(), savedRequest.getStatus());
    }



    //this method creats a rewquest for community creation
    public CommunityResponse storeCreateRequest(HelpRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        // Create the help request
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setUser(user);
        helpRequest.setNeighbourhood(null);
        helpRequest.setRequestType(HelpRequest.RequestType.CREATE);
        helpRequest.setDescription(dto.getDescription());
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        helpRequest.setCreatedAt(LocalDateTime.now());


        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);

        // Convert HelpRequest to CommunityResponse before returning
        return new CommunityResponse(savedRequest.getUser().getId(), 0, savedRequest.getStatus());
    }

    public List<HelpRequest> getAllJoinCommunityRequests(int neighbourhoodId) {
        Neighbourhood neighbourhood = neighbourhoodRepository.findByNeighbourhoodId(neighbourhoodId)
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found"));

        // Fetch only JOIN requests that have status OPEN
        return helpRequestRepository.findByNeighbourhoodAndRequestTypeAndStatus(
                neighbourhood, HelpRequest.RequestType.JOIN, HelpRequest.RequestStatus.OPEN
        );
    }

    public List<HelpRequest> getRequestsForAdmin(int neighbourhoodId) {
        Neighbourhood neighbourhood = neighbourhoodRepository.findByNeighbourhoodId(neighbourhoodId)
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found"));
        return helpRequestRepository.findByNeighbourhood(neighbourhood);
    }

    public List<HelpRequestDTO> getAllOpenCommunityRequests() {

        List<HelpRequest> helpRequests = helpRequestRepository.findByStatus(HelpRequest.RequestStatus.OPEN);
        return helpRequests.stream()
                .map(helpRequest -> new HelpRequestDTO().buiHelpRequestDTO(helpRequest))
                .collect(Collectors.toList());}

}

