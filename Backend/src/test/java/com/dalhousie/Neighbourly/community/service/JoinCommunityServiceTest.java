package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
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
class JoinCommunityServiceTest {

    private static final int TEST_REQUEST_ID = 100;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 10;
    private static final String TEST_CONTACT = "1234567890";
    private static final String TEST_ADDRESS = "123 Test St";
    private static final String TEST_DESCRIPTION = "Phone: 9876543210, Address: 456 Main St";
    private static final String UPDATED_CONTACT = "9876543210";
    private static final String UPDATED_ADDRESS = "456 Main St";

    @Mock
    private HelpRequestService helpRequestService;

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JoinCommunityService joinCommunityService;

    private HelpRequest helpRequest;
    private User user;
    private Neighbourhood neighbourhood;

    @BeforeEach
    void setUp() {
        // Create mock user
        user = new User();
        user.setId(TEST_USER_ID);
        user.setUserType(UserType.USER);
        user.setContact(TEST_CONTACT);
        user.setAddress(TEST_ADDRESS);

        // Create mock neighbourhood
        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        neighbourhood.setName("Test Community");
        neighbourhood.setLocation("Test City");

        // Create mock help request
        helpRequest = new HelpRequest();
        helpRequest.setRequestId(TEST_REQUEST_ID);
        helpRequest.setUser(user);
        helpRequest.setNeighbourhood(neighbourhood);
        helpRequest.setDescription(TEST_DESCRIPTION);
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
    }

    @Test
    void testApproveJoinRequest_Success() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);

        CustomResponseBody<CommunityResponse> response = joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertEquals(UserType.RESIDENT, user.getUserType());
        assertEquals(neighbourhood.getNeighbourhoodId(), user.getNeighbourhood_id());
        assertEquals(UPDATED_CONTACT, user.getContact());
        assertEquals(UPDATED_ADDRESS, user.getAddress());
        assertEquals(HelpRequest.RequestStatus.APPROVED, helpRequest.getStatus());
        verify(userRepository, times(1)).save(user);
        verify(helpRequestRepository, times(1)).save(helpRequest);
    }

    @Test
    void testApproveJoinRequest_RequestNotFound() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = joinCommunityService.approveJoinRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertEquals("Join request not found", response.message());
        verify(userRepository, never()).save(any());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void testDenyJoinRequest_Success() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);

        CustomResponseBody<CommunityResponse> response = joinCommunityService.denyJoinRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertEquals(HelpRequest.RequestStatus.DECLINED, helpRequest.getStatus());
        verify(helpRequestRepository, times(1)).save(helpRequest);
    }

    @Test
    void testDenyJoinRequest_RequestNotFound() {
        when(helpRequestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = joinCommunityService.denyJoinRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertEquals("Join request not found", response.message());
        verify(helpRequestRepository, never()).save(any());
    }
}
