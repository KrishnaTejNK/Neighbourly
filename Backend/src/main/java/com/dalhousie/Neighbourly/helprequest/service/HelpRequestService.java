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

@Service
@RequiredArgsConstructor
public class HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;

    private User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Neighbourhood getNeighbourhoodById(int neighbourhoodId) {
        return neighbourhoodRepository.findById(neighbourhoodId)
                .orElseThrow(() -> new RuntimeException("Neighbourhood not found"));
    }

    private HelpRequest createHelpRequest(HelpRequestDTO dto, User user, Neighbourhood neighbourhood, HelpRequest.RequestType requestType) {
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setUser(user);
        helpRequest.setNeighbourhood(neighbourhood);
        helpRequest.setRequestType(requestType);
        helpRequest.setDescription(dto.getDescription());
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        helpRequest.setCreatedAt(LocalDateTime.now());
        return helpRequest;
    }

    private CommunityResponse createCommunityResponse(HelpRequest savedRequest) {
        return new CommunityResponse(savedRequest.getUser().getId(), savedRequest.getNeighbourhood() != null ? savedRequest.getNeighbourhood().getNeighbourhoodId() : 0, savedRequest.getStatus());
    }

    public CommunityResponse storeJoinRequest(HelpRequestDTO dto) {
        User user = getUserById(dto.getUserId());
        Neighbourhood neighbourhood = getNeighbourhoodById(dto.getNeighbourhoodId());

        HelpRequest helpRequest = createHelpRequest(dto, user, neighbourhood, HelpRequest.RequestType.JOIN);
        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);

        return createCommunityResponse(savedRequest);
    }

    public CommunityResponse storeCreateRequest(HelpRequestDTO dto) {
        User user = getUserById(dto.getUserId());

        HelpRequest helpRequest = createHelpRequest(dto, user, null, HelpRequest.RequestType.CREATE);
        HelpRequest savedRequest = helpRequestRepository.save(helpRequest);

        return createCommunityResponse(savedRequest);
    }

    public List<HelpRequest> getAllJoinCommunityRequests(int neighbourhoodId) {
        Neighbourhood neighbourhood = getNeighbourhoodById(neighbourhoodId);

        return helpRequestRepository.findByNeighbourhoodAndRequestTypeAndStatus(
                neighbourhood, HelpRequest.RequestType.JOIN, HelpRequest.RequestStatus.OPEN
        );
    }

    public List<HelpRequestDTO> getAllOpenCommunityRequests() {

        List<HelpRequest> helpRequests = helpRequestRepository.findByStatusAndRequestType(HelpRequest.RequestStatus.OPEN, HelpRequest.RequestType.CREATE);
        return helpRequests.stream()
                .map(helpRequest -> new HelpRequestDTO().buiHelpRequestDTO(helpRequest))
                .collect(Collectors.toList());
    }
}

