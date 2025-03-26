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
        user.setId(1);
        user.setUserType(UserType.RESIDENT);
        user.setNeighbourhood_id(0);
        user.setContact("1234567890");
        user.setAddress("123 Main St");

        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(100);
        neighbourhood.setLocation("Downtown");
        neighbourhood.setName("Downtown");

        helpRequest = new HelpRequest();
        helpRequest.setRequestId(10);
        helpRequest.setUser(user);
        helpRequest.setRequestType(HelpRequest.RequestType.CREATE);
        helpRequest.setStatus(HelpRequest.RequestStatus.OPEN);
        helpRequest.setDescription("location: Downtown | Phone: 1234567890 | Address: 123 Main St");
    }

    @Test
    void testApproveCreateRequest_Success() {
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.of(helpRequest));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.save(any(Neighbourhood.class))).thenReturn(neighbourhood);
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);
        when(userRepository.save(any(User.class))).thenReturn(user);

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(10);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertNotNull(response.data());
        assertEquals(1, response.data().getUserId());
        assertEquals(100, response.data().getNeighbourhoodId());
        assertEquals(HelpRequest.RequestStatus.APPROVED, response.data().getStatus());
        assertEquals("Community successfully created", response.message());

        verify(helpRequestRepository, times(1)).save(helpRequest);
        verify(userRepository, times(1)).save(user);
        verify(neighbourhoodRepository, times(1)).save(any(Neighbourhood.class));
    }

    @Test
    void testApproveCreateRequest_RequestNotFound() {
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(10);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Create request not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(10);
        verifyNoMoreInteractions(userRepository, neighbourhoodRepository);
    }

    @Test
    void testApproveCreateRequest_InvalidRequestType() {
        helpRequest.setRequestType(HelpRequest.RequestType.JOIN);
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.of(helpRequest));

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(10);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Invalid request type", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(10);
        verifyNoMoreInteractions(userRepository, neighbourhoodRepository);
    }

    @Test
    void testApproveCreateRequest_UserNotFound() {
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.of(helpRequest));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.approveCreateRequest(10);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("User not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(10);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testDenyCreateRequest_Success() {
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.of(helpRequest));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);

        CustomResponseBody<CommunityResponse> response = createCommunityService.denyCreateRequest(10);

        assertNotNull(response);
        assertEquals(CustomResponseBody.Result.SUCCESS, response.result());
        assertNotNull(response.data());
        assertEquals(1, response.data().getUserId());
        assertEquals(0, response.data().getNeighbourhoodId());
        assertEquals(HelpRequest.RequestStatus.APPROVED, response.data().getStatus());
        assertEquals("Community creation request denied", response.message());

        verify(helpRequestRepository, times(1)).save(helpRequest);
    }

    @Test
    void testDenyCreateRequest_RequestNotFound() {
        when(helpRequestRepository.findByRequestId(10)).thenReturn(Optional.empty());

        CustomResponseBody<CommunityResponse> response = createCommunityService.denyCreateRequest(10);

        assertEquals(CustomResponseBody.Result.FAILURE, response.result());
        assertNull(response.data());
        assertEquals("Create request not found", response.message());

        verify(helpRequestRepository, times(1)).findByRequestId(10);
    }
}