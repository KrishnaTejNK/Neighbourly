package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NeighbourhoodServiceTest {

    private static final int NEIGHBOURHOOD_ID_1 = 1;
    private static final int NEIGHBOURHOOD_ID_2 = 2;
    private static final String NEIGHBOURHOOD_NAME_1 = "Downtown";
    private static final String NEIGHBOURHOOD_NAME_2 = "Uptown";
    private static final String LOCATION_1 = "Central City";
    private static final String LOCATION_2 = "North City";
    private static final long MEMBER_COUNT_1 = 10L;
    private static final long MEMBER_COUNT_2 = 5L;
    private static final String MANAGER_NAME_1 = "Alice";
    private static final String MANAGER_ID_1 = "M001";
    private static final String NO_MANAGER_ASSIGNED = "No Manager Assigned";
    private static final String EMPTY_STRING = "";

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NeighbourhoodService neighbourhoodService;

    private Neighbourhood neighbourhood1;
    private Neighbourhood neighbourhood2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Neighbourhood Data
        neighbourhood1 = new Neighbourhood();
        neighbourhood1.setNeighbourhoodId(NEIGHBOURHOOD_ID_1);
        neighbourhood1.setName(NEIGHBOURHOOD_NAME_1);
        neighbourhood1.setLocation(LOCATION_1);

        neighbourhood2 = new Neighbourhood();
        neighbourhood2.setNeighbourhoodId(NEIGHBOURHOOD_ID_2);
        neighbourhood2.setName(NEIGHBOURHOOD_NAME_2);
        neighbourhood2.setLocation(LOCATION_2);
    }

    @Test
    void testGetAllNeighbourhoods() {
        // Mock repository behavior
        when(neighbourhoodRepository.findAll()).thenReturn(Arrays.asList(neighbourhood1, neighbourhood2));

        // Mock user repository methods for member count and manager details
        when(userRepository.countByNeighbourhoodId(NEIGHBOURHOOD_ID_1)).thenReturn(MEMBER_COUNT_1);
        when(userRepository.findManagerNameByNeighbourhoodId(NEIGHBOURHOOD_ID_1)).thenReturn(MANAGER_NAME_1);
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(NEIGHBOURHOOD_ID_1)).thenReturn(MANAGER_ID_1);

        when(userRepository.countByNeighbourhoodId(NEIGHBOURHOOD_ID_2)).thenReturn(MEMBER_COUNT_2);
        when(userRepository.findManagerNameByNeighbourhoodId(NEIGHBOURHOOD_ID_2)).thenReturn(null); // No manager
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(NEIGHBOURHOOD_ID_2)).thenReturn(null);

        // Execute the service method
        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        // Validate results
        assertNotNull(result);
        assertEquals(2, result.size());

        // First Neighbourhood Assertions
        NeighbourhoodResponse response1 = result.get(0);
        assertEquals(NEIGHBOURHOOD_ID_1, response1.getNeighbourhoodId());
        assertEquals(NEIGHBOURHOOD_NAME_1, response1.getName());
        assertEquals(LOCATION_1, response1.getLocation());
        assertEquals(String.valueOf(MEMBER_COUNT_1), response1.getMemberCount());
        assertEquals(MANAGER_NAME_1, response1.getManagerName());
        assertEquals(MANAGER_ID_1, response1.getManagerId());

        // Second Neighbourhood Assertions
        NeighbourhoodResponse response2 = result.get(1);
        assertEquals(NEIGHBOURHOOD_ID_2, response2.getNeighbourhoodId());
        assertEquals(NEIGHBOURHOOD_NAME_2, response2.getName());
        assertEquals(LOCATION_2, response2.getLocation());
        assertEquals(String.valueOf(MEMBER_COUNT_2), response2.getMemberCount());
        assertEquals(NO_MANAGER_ASSIGNED, response2.getManagerName());
        assertEquals(EMPTY_STRING, response2.getManagerId());

        // Verify repository method calls
        verify(neighbourhoodRepository, times(1)).findAll();
        verify(userRepository, times(1)).countByNeighbourhoodId(NEIGHBOURHOOD_ID_1);
        verify(userRepository, times(1)).findManagerNameByNeighbourhoodId(NEIGHBOURHOOD_ID_1);
        verify(userRepository, times(1)).userRepositoryFindManagerIdByNeighbourhoodId(NEIGHBOURHOOD_ID_1);
        verify(userRepository, times(1)).countByNeighbourhoodId(NEIGHBOURHOOD_ID_2);
        verify(userRepository, times(1)).findManagerNameByNeighbourhoodId(NEIGHBOURHOOD_ID_2);
        verify(userRepository, times(1)).userRepositoryFindManagerIdByNeighbourhoodId(NEIGHBOURHOOD_ID_2);
    }
}
