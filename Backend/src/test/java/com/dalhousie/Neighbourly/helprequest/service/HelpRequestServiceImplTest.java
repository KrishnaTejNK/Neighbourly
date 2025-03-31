package com.dalhousie.Neighbourly.helprequest.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HelpRequestServiceImplTest {

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @InjectMocks
    private HelpRequestServiceImpl helpRequestService;

    private User testUser;
    private Neighbourhood testNeighbourhood;
    private HelpRequestDTO testDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);

        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        testDto = new HelpRequestDTO();
        testDto.setUserId(TEST_USER_ID);
        testDto.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        testDto.setDescription("Test request");
    }

    @Test
    void storeJoinRequest_successful_returnsCommunityResponse() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.of(testNeighbourhood));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommunityResponse result = helpRequestService.storeJoinRequest(testDto);

        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(TEST_NEIGHBOURHOOD_ID, result.getNeighbourhoodId());
        assertEquals(RequestStatus.OPEN, result.getStatus());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(any(HelpRequest.class));
    }

    @Test
    void storeJoinRequest_userNotFound_throwsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                helpRequestService.storeJoinRequest(testDto));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
        verify(neighbourhoodRepository, never()).findById(anyInt());
    }

    @Test
    void storeJoinRequest_neighbourhoodNotFound_throwsException() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                helpRequestService.storeJoinRequest(testDto));
        assertEquals("Neighbourhood not found", exception.getMessage());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void storeCreateRequest_successful_returnsCommunityResponse() {
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommunityResponse result = helpRequestService.storeCreateRequest(testDto);

        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(0, result.getNeighbourhoodId());
        assertEquals(RequestStatus.OPEN, result.getStatus());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(any(HelpRequest.class));
        verify(neighbourhoodRepository, never()).findById(anyInt());
    }

    @Test
    void getAllJoinCommunityRequests_successful_returnsRequestList() {
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setUser(testUser);
        helpRequest.setNeighbourhood(testNeighbourhood);
        helpRequest.setRequestType(HelpRequest.RequestType.JOIN);
        helpRequest.setStatus(RequestStatus.OPEN);

        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.of(testNeighbourhood));
        when(helpRequestRepository.findByNeighbourhoodAndRequestTypeAndStatus(
                eq(testNeighbourhood), eq(HelpRequest.RequestType.JOIN), eq(RequestStatus.OPEN)))
                .thenReturn(List.of(helpRequest));

        List<HelpRequest> result = helpRequestService.getAllJoinCommunityRequests(TEST_NEIGHBOURHOOD_ID);

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        assertEquals(helpRequest, result.get(0));
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).findByNeighbourhoodAndRequestTypeAndStatus(
                eq(testNeighbourhood), eq(HelpRequest.RequestType.JOIN), eq(RequestStatus.OPEN));
    }

    @Test
    void getAllOpenCommunityRequests_successful_returnsDTOList() {
        HelpRequest helpRequest = new HelpRequest();
        helpRequest.setUser(testUser);
        helpRequest.setRequestType(HelpRequest.RequestType.CREATE);
        helpRequest.setStatus(RequestStatus.OPEN);
        helpRequest.setDescription("Test request");
        helpRequest.setCreatedAt(LocalDateTime.now());

        when(helpRequestRepository.findByStatusAndRequestType(RequestStatus.OPEN, HelpRequest.RequestType.CREATE))
                .thenReturn(List.of(helpRequest));

        List<HelpRequestDTO> result = helpRequestService.getAllOpenCommunityRequests();

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        HelpRequestDTO dto = result.get(0);
        assertEquals(TEST_USER_ID, dto.getUserId());
        assertEquals("Test request", dto.getDescription());
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).findByStatusAndRequestType(RequestStatus.OPEN, HelpRequest.RequestType.CREATE);
    }

    @Test
    void getAllJoinCommunityRequests_neighbourhoodNotFound_throwsException() {
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                helpRequestService.getAllJoinCommunityRequests(TEST_NEIGHBOURHOOD_ID));
        assertEquals("Neighbourhood not found", exception.getMessage());
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
        verify(helpRequestRepository, never()).findByNeighbourhoodAndRequestTypeAndStatus(any(), any(), any());
    }
}