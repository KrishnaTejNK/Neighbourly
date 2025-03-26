package com.dalhousie.Neighbourly.helprequest.service;

import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpRequestServiceTest {

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @InjectMocks
    private HelpRequestService helpRequestService;

    private User user;
    private Neighbourhood neighbourhood;
    private HelpRequestDTO helpRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock User
        user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setName("John Doe");
        user.setPassword("password");
        user.setUserType(UserType.RESIDENT);

        // Mock Neighbourhood
        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(1);
        neighbourhood.setName("Sample Neighbourhood");
        neighbourhood.setLocation("Sample Location");

        // Mock HelpRequestDTO
        helpRequestDTO = new HelpRequestDTO();
        helpRequestDTO.setUserId(user.getId());
        helpRequestDTO.setNeighbourhoodId(neighbourhood.getNeighbourhoodId());
        helpRequestDTO.setDescription("Looking to join a community.");
    }

    @Test
    void testStoreJoinRequest() {
        when(userRepository.findById(helpRequestDTO.getUserId())).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(helpRequestDTO.getNeighbourhoodId())).thenReturn(Optional.of(neighbourhood));

        HelpRequest savedRequest = new HelpRequest();
        savedRequest.setUser(user);
        savedRequest.setNeighbourhood(neighbourhood);
        savedRequest.setRequestType(HelpRequest.RequestType.JOIN);
        savedRequest.setDescription(helpRequestDTO.getDescription());
        savedRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        savedRequest.setCreatedAt(LocalDateTime.now());

        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(savedRequest);

        CommunityResponse response = helpRequestService.storeJoinRequest(helpRequestDTO);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(neighbourhood.getNeighbourhoodId(), response.getNeighbourhoodId());
        assertEquals(HelpRequest.RequestStatus.OPEN, response.getStatus());
    }

    @Test
    void testStoreCreateRequest() {
        when(userRepository.findById(helpRequestDTO.getUserId())).thenReturn(Optional.of(user));

        HelpRequest savedRequest = new HelpRequest();
        savedRequest.setUser(user);
        savedRequest.setNeighbourhood(null);
        savedRequest.setRequestType(HelpRequest.RequestType.CREATE);
        savedRequest.setDescription(helpRequestDTO.getDescription());
        savedRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        savedRequest.setCreatedAt(LocalDateTime.now());

        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(savedRequest);

        CommunityResponse response = helpRequestService.storeCreateRequest(helpRequestDTO);

        assertNotNull(response);
        assertEquals(user.getId(), response.getUserId());
        assertEquals(0, response.getNeighbourhoodId());  // Since it's a create request
        assertEquals(HelpRequest.RequestStatus.OPEN, response.getStatus());
    }

    @Test
    void testGetAllJoinCommunityRequests() {
        when(neighbourhoodRepository.findByNeighbourhoodId(1)).thenReturn(Optional.of(neighbourhood));

        HelpRequest request = new HelpRequest();
        request.setUser(user);
        request.setNeighbourhood(neighbourhood);
        request.setRequestType(HelpRequest.RequestType.JOIN);
        request.setStatus(HelpRequest.RequestStatus.OPEN);
        request.setCreatedAt(LocalDateTime.now());

        when(helpRequestRepository.findByNeighbourhoodAndRequestTypeAndStatus(any(Neighbourhood.class), any(), any()))
                .thenReturn(List.of(request));

        List<HelpRequest> requests = helpRequestService.getAllJoinCommunityRequests(1);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(HelpRequest.RequestType.JOIN, requests.get(0).getRequestType());
        assertEquals(HelpRequest.RequestStatus.OPEN, requests.get(0).getStatus());
    }

    @Test
    void testGetAllOpenCommunityRequests() {
        HelpRequest request = new HelpRequest();
        request.setUser(user);
        request.setNeighbourhood(null);
        request.setRequestType(HelpRequest.RequestType.CREATE);
        request.setStatus(HelpRequest.RequestStatus.OPEN);
        request.setCreatedAt(LocalDateTime.now());

        when(helpRequestRepository.findByStatusAndRequestType(HelpRequest.RequestStatus.OPEN, HelpRequest.RequestType.CREATE))
                .thenReturn(List.of(request));

        List<HelpRequestDTO> requests = helpRequestService.getAllOpenCommunityRequests();

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(HelpRequest.RequestType.CREATE, request.getRequestType());
        assertEquals(HelpRequest.RequestStatus.OPEN, requests.get(0).getStatus());
    }

    @Test
    void testStoreJoinRequestUserNotFound() {
        when(userRepository.findById(helpRequestDTO.getUserId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            helpRequestService.storeJoinRequest(helpRequestDTO);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testStoreJoinRequestNeighbourhoodNotFound() {
        when(userRepository.findById(helpRequestDTO.getUserId())).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(helpRequestDTO.getNeighbourhoodId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            helpRequestService.storeJoinRequest(helpRequestDTO);
        });
        assertEquals("Neighbourhood not found", exception.getMessage());
    }

}
