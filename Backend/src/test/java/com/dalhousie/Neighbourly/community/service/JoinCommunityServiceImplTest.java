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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int TEST_REQUEST_ID = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

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
        testUser.setId(TEST_USER_ID);

        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        testRequest = new HelpRequest();
        testRequest.setUser(testUser);
        testRequest.setNeighbourhood(testNeighbourhood);
        testRequest.setDescription("Phone: 123-456-7890, Address: 123 Test St");
        testRequest.setStatus(RequestStatus.OPEN);

        testDto = new HelpRequestDTO();
        testDto.setUserId(TEST_USER_ID);
        testDto.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void storeJoinRequest_delegatesToHelpRequestService() {
        CommunityResponse expectedResponse = new CommunityResponse(TEST_USER_ID, TEST_NEIGHBOURHOOD_ID, RequestStatus.OPEN);
        when(helpRequestService.storeJoinRequest(testDto)).thenReturn(expectedResponse);

        CommunityResponse result = joinCommunityService.storeJoinRequest(testDto);

        assertEquals(expectedResponse, result);
        verify(helpRequestService, times(EXPECTED_CALL_COUNT)).storeJoinRequest(testDto);
    }

    @Test
    void approveJoinRequest_successful_returnsSuccessResponse() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertNotNull(result.data());
        assertEquals("User approved and added as a resident with contact and address.", result.message());
        assertEquals(UserType.RESIDENT, testUser.getUserType());
        assertEquals(TEST_NEIGHBOURHOOD_ID, testUser.getNeighbourhood_id());
        assertEquals("123-456-7890", testUser.getContact());
        assertEquals("123 Test St", testUser.getAddress());
        assertEquals(RequestStatus.APPROVED, testRequest.getStatus());
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(testRequest);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).save(testUser);
    }

    @Test
    void approveJoinRequest_requestNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("Join request not found", result.message());
        verify(userRepository, never()).findById(anyInt());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void approveJoinRequest_userNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("User not found", result.message());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void denyJoinRequest_successful_returnsSuccessResponse() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));

        CustomResponseBody<CommunityResponse> result = joinCommunityService.denyJoinRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertNotNull(result.data());
        assertEquals("User denied and request status updated", result.message());
        assertEquals(RequestStatus.DECLINED, testRequest.getStatus());
        assertEquals(TEST_USER_ID, result.data().getUserId());
        assertEquals(TEST_NEIGHBOURHOOD_ID, result.data().getNeighbourhoodId());
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(testRequest);
        verify(userRepository, never()).save(any());
    }

    @Test
    void denyJoinRequest_requestNotFound_returnsFailureResponse() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> result = joinCommunityService.denyJoinRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertNull(result.data());
        assertEquals("Join request not found", result.message());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void updateUserDetails_noPhoneOrAddress_updatesOnlyRequiredFields() {
        testRequest.setDescription("No contact info here");
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertEquals(UserType.RESIDENT, testUser.getUserType());
        assertEquals(TEST_NEIGHBOURHOOD_ID, testUser.getNeighbourhood_id());
        assertNull(testUser.getContact());
        assertNull(testUser.getAddress());
        verify(userRepository, times(EXPECTED_CALL_COUNT)).save(testUser);
    }
}