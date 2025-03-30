package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.repository.HelpRequestRepository;
import com.dalhousie.Neighbourly.helprequest.service.HelpRequestService;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
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
class CreateCommunityServiceTest {

    private static final int TEST_REQUEST_ID = 10;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 100;

    @Mock
    private HelpRequestService helpRequestService;

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateCommunityService createCommunityService;

    private HelpRequest helpRequest;
    private User user;
    private Neighbourhood neighbourhood;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(TEST_USER_ID);
        user.setUserType(UserType.RESIDENT);
        user.setNeighbourhood_id(0);
        user.setContact("1234567890");
        user.setAddress("123 Main St");

        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        neighbourhood.setLocation("Downtown");
        neighbourhood.setName("Downtown");

        helpRequest = new HelpRequest();
        helpRequest.setRequestId(TEST_REQUEST_ID);
        helpRequest.setUser(user);
        helpRequest.setRequestType(HelpRequest.RequestType.CREATE);
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        helpRequest.setDescription("location: Downtown | Phone: 1234567890 | Address: 123 Main St");
    }

    @Test
    void testApproveCreateRequest_Success() {
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.save(any(Neighbourhood.class))).thenReturn(neighbourhood);
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);
        when(userRepository.save(any(User.class))).thenReturn(user);

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertNotNull(response.data());
        assertEquals(TEST_USER_ID, response.data().getUserId());
        assertEquals(TEST_NEIGHBOURHOOD_ID, response.data().getNeighbourhoodId());
        assertEquals(HelpRequest.RequestStatus.APPROVED, response.data().getStatus());
        assertEquals("Community successfully created", response.message());

        verify(helpRequestRepository, times(1)).save(helpRequest);
        verify(userRepository, times(1)).save(user);
        verify(neighbourhoodRepository, times(1)).save(any(Neighbourhood.class));
    }

    @Test
    void testApproveCreateRequest_RequestNotFound() {
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Create request not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(TEST_REQUEST_ID);
        verifyNoMoreInteractions(userRepository, neighbourhoodRepository);
    }

    @Test
    void testApproveCreateRequest_InvalidRequestType() {
        helpRequest.setRequestType(HelpRequest.RequestType.JOIN);
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Invalid request type", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(TEST_REQUEST_ID);
        verifyNoMoreInteractions(userRepository, neighbourhoodRepository);
    }

    @Test
    void testApproveCreateRequest_UserNotFound() {
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("User not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(TEST_REQUEST_ID);
        verify(userRepository, times(1)).findById(TEST_USER_ID);
    }

    @Test
    void testDenyCreateRequest_Success() {
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(helpRequest));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);

        CustomResponseBody<CommunityResponse> response = createCommunityService.denyCreateRequest(TEST_REQUEST_ID);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertNotNull(response.data());
        assertEquals(TEST_USER_ID, response.data().getUserId());
        assertEquals(0, response.data().getNeighbourhoodId());
        assertEquals(HelpRequest.RequestStatus.APPROVED, response.data().getStatus());
        assertEquals("Community creation request denied", response.message());

        verify(helpRequestRepository, times(1)).save(helpRequest);
    }

    @Test
    void testDenyCreateRequest_RequestNotFound() {
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.denyCreateRequest(TEST_REQUEST_ID);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Create request not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(TEST_REQUEST_ID);
    }
}
