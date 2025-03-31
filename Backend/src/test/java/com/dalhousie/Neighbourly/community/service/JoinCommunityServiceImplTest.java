package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.helprequest.service.HelpRequestService;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.entity.UserType;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.util.CustomResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinCommunityServiceImplTest {

    @Mock
    private HelpRequestService helpRequestService;

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JoinCommunityServiceImpl joinCommunityService;

    private User testUser;
    private Neighbourhood testNeighbourhood;
    private HelpRequest testRequest;
    private HelpRequestDTO testDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);

        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(1);

        testRequest = new HelpRequest();
        testRequest.setUser(testUser);
        testRequest.setNeighbourhood(testNeighbourhood);
        testRequest.setDescription("Phone: 123-456-7890, Address: 123 Test St");
        testRequest.setStatus(RequestStatus.OPEN);

        testDto = new HelpRequestDTO();
        testDto.setUserId(1);
        testDto.setNeighbourhoodId(1);
    }

    @Test
    void storeJoinRequest_delegatesToHelpRequestService() {
        CommunityResponse expectedResponse = new CommunityResponse(1, 1, RequestStatus.OPEN);
        when(helpRequestService.storeJoinRequest(testDto)).thenReturn(expectedResponse);

        CommunityResponse result = joinCommunityService.storeJoinRequest(testDto);

        assertEquals(expectedResponse, result);
        verify(helpRequestService).storeJoinRequest(testDto);
    }

    @Test
    void approveJoinRequest_successful_returnsSuccessResponse() {
        when(helpRequestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(1);

        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertNotNull(result.data());
        assertEquals("User approved and added as a resident with contact and address.", result.message());
        assertEquals(UserType.RESIDENT, testUser.getUserType());
        assertEquals(1, testUser.getNeighbourhood_id());
        assertEquals("123-456-7890", testUser.getContact());
        assertEquals("123 Test St", testUser.getAddress());
        assertEquals(RequestStatus.APPROVED, testRequest.getStatus());
        verify(helpRequestRepository).save(testRequest);
        verify(userRepository).save(testUser);
    }

    @Test
    void approveJoinRequest_requestNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(1)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(1);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("Join request not found", result.message());
        verify(userRepository, never()).findById(anyInt());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void approveJoinRequest_userNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(1);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("User not found", result.message());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void denyJoinRequest_successful_returnsSuccessResponse() {
        when(helpRequestRepository.findById(1)).thenReturn(Optional.of(testRequest));

        CustomResponseBody<CommunityResponse> result = joinCommunityService.denyJoinRequest(1);

        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertNotNull(result.data());
        assertEquals("User denied and request status updated", result.message());
        assertEquals(RequestStatus.DECLINED, testRequest.getStatus());
        assertEquals(1, result.data().getUserId());
        assertEquals(1, result.data().getNeighbourhoodId());
        verify(helpRequestRepository).save(testRequest);
        verify(userRepository, never()).save(any());
    }

    @Test
    void denyJoinRequest_requestNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(1)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.denyJoinRequest(1);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("Join request not found", result.message());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void updateUserDetails_noPhoneOrAddress_updatesOnlyRequiredFields() {
        testRequest.setDescription("No contact info here");
        when(helpRequestRepository.findById(1)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        joinCommunityService.approveJoinRequest(1);

        assertEquals(UserType.RESIDENT, testUser.getUserType());
        assertEquals(1, testUser.getNeighbourhood_id());
        assertNull(testUser.getContact());
        assertNull(testUser.getAddress());
        verify(userRepository).save(testUser);
    }
}