package com.dalhousie.Neighbourly.community.service;

import com.dalhousie.Neighbourly.community.entities.CommunityResponse;
import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateCommunityServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class CreateCommunityServiceImplTest {

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 2;
    private static final int TEST_REQUEST_ID = 1;
    private static final int MISSING_REQUEST_ID = 999;
    private static final int EXPECTED_CALL_COUNT = 1;

    @Mock
    private HelpRequestService helpRequestService;

    @Mock
    private HelpRequestRepository helpRequestRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateCommunityServiceImpl createCommunityService;

    private HelpRequest mockRequest;
    private User mockUser;
    private Neighbourhood mockNeighbourhood;
    private CommunityResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(TEST_USER_ID);

        mockNeighbourhood = new Neighbourhood();
        mockNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        mockRequest = new HelpRequest();
        mockRequest.setRequestId(TEST_REQUEST_ID);
        mockRequest.setUser(mockUser);
        mockRequest.setNeighbourhood(mockNeighbourhood);
        mockRequest.setRequestType(HelpRequest.RequestType.CREATE);
        mockRequest.setDescription("location: Downtown | Phone: 123-456-7890 | Address: 123 Main St");
        mockRequest.setStatus(RequestStatus.OPEN);

        mockResponse = new CommunityResponse(TEST_USER_ID, TEST_NEIGHBOURHOOD_ID, RequestStatus.APPROVED);
    }

    @Test
    void storeCreateRequest_delegatesToHelpRequestService() {
        // Arrange
        HelpRequestDTO dto = new HelpRequestDTO();
        when(helpRequestService.storeCreateRequest(dto)).thenReturn(mockResponse);

        // Act
        CommunityResponse result = createCommunityService.storeCreateRequest(dto);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(helpRequestService, times(EXPECTED_CALL_COUNT)).storeCreateRequest(dto);
    }

    @Test
    void approveCreateRequest_approvesSuccessfully() {
        // Arrange
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(mockRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
        when(neighbourhoodRepository.save(any(Neighbourhood.class))).thenReturn(mockNeighbourhood);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(mockRequest);

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertEquals("Community successfully created", result.message());
        CommunityResponse response = result.data();
        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(TEST_NEIGHBOURHOOD_ID, response.getNeighbourhoodId());
        assertEquals(RequestStatus.APPROVED, response.getStatus());
        assertEquals(UserType.COMMUNITY_MANAGER, mockUser.getUserType());
        assertEquals("123-456-7890", mockUser.getContact());
        assertEquals("123 Main St", mockUser.getAddress());
        assertEquals(RequestStatus.APPROVED, mockRequest.getStatus());
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).save(any(Neighbourhood.class));
        verify(userRepository, times(EXPECTED_CALL_COUNT)).save(mockUser);
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(mockRequest);
    }

    @Test
    void approveCreateRequest_returnsFailure_whenRequestNotFound() {
        // Arrange
        when(helpRequestRepository.findByRequestId(MISSING_REQUEST_ID)).thenReturn(Optional.empty());

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.approveCreateRequest(MISSING_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertEquals("Create request not found", result.message());
        assertNull(result.data());
        verify(neighbourhoodRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void approveCreateRequest_returnsFailure_whenRequestTypeInvalid() {
        // Arrange
        mockRequest.setRequestType(HelpRequest.RequestType.JOIN); // Not CREATE
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(mockRequest));

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertEquals("Invalid request type", result.message());
        assertNull(result.data());
        verify(userRepository, never()).findById(anyInt());
        verify(neighbourhoodRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void approveCreateRequest_returnsFailure_whenUserNotFound() {
        // Arrange
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(mockRequest));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.approveCreateRequest(TEST_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertEquals("User not found", result.message());
        assertNull(result.data());
        verify(neighbourhoodRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(helpRequestRepository, never()).save(any());
    }

    @Test
    void denyCreateRequest_deniesSuccessfully() {
        // Arrange
        when(helpRequestRepository.findByRequestId(TEST_REQUEST_ID)).thenReturn(Optional.of(mockRequest));
        when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(mockRequest);

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.denyCreateRequest(TEST_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.SUCCESS, result.result());
        assertEquals("Community creation request denied", result.message());
        CommunityResponse response = result.data();
        assertEquals(TEST_USER_ID, response.getUserId());
        assertEquals(0, response.getNeighbourhoodId()); // 0 since no neighbourhood created
        assertEquals(RequestStatus.DECLINED, response.getStatus());
        assertEquals(RequestStatus.DECLINED, mockRequest.getStatus());
        verify(helpRequestRepository, times(EXPECTED_CALL_COUNT)).save(mockRequest);
        verify(neighbourhoodRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void denyCreateRequest_returnsFailure_whenRequestNotFound() {
        // Arrange
        when(helpRequestRepository.findByRequestId(MISSING_REQUEST_ID)).thenReturn(Optional.empty());

        // Act
        CustomResponseBody<CommunityResponse> result = createCommunityService.denyCreateRequest(MISSING_REQUEST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CustomResponseBody.Result.FAILURE, result.result());
        assertEquals("Create request not found", result.message());
        assertNull(result.data());
        verify(helpRequestRepository, never()).save(any());
        verify(neighbourhoodRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }
}