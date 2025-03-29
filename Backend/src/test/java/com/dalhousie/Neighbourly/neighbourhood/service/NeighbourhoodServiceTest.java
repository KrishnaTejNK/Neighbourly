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
        neighbourhood1.setNeighbourhoodId(1);
        neighbourhood1.setName("Downtown");
        neighbourhood1.setLocation("Central City");

        neighbourhood2 = new Neighbourhood();
        neighbourhood2.setNeighbourhoodId(2);
        neighbourhood2.setName("Uptown");
        neighbourhood2.setLocation("North City");
    }

    @Test
    void testGetAllNeighbourhoods() {
        // Mock repository behavior
        when(neighbourhoodRepository.findAll()).thenReturn(Arrays.asList(neighbourhood1, neighbourhood2));

        // Mock user repository methods for member count and manager details
        when(userRepository.countByNeighbourhoodId(1)).thenReturn(10L);
        when(userRepository.findManagerNameByNeighbourhoodId(1)).thenReturn("Alice");
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(1)).thenReturn("M001");

        when(userRepository.countByNeighbourhoodId(2)).thenReturn(5L);
        when(userRepository.findManagerNameByNeighbourhoodId(2)).thenReturn(null);  // No manager
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(2)).thenReturn(null);

        // Execute the service method
        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        // Validate results
        assertNotNull(result);
        assertEquals(2, result.size());

        // First Neighbourhood Assertions
        NeighbourhoodResponse response1 = result.get(0);
        assertEquals(1, response1.getNeighbourhoodId());
        assertEquals("Downtown", response1.getName());
        assertEquals("Central City", response1.getLocation());
        assertEquals("10", response1.getMemberCount());
        assertEquals("Alice", response1.getManagerName());
        assertEquals("M001", response1.getManagerId());

        // Second Neighbourhood Assertions
        NeighbourhoodResponse response2 = result.get(1);
        assertEquals(2, response2.getNeighbourhoodId());
        assertEquals("Uptown", response2.getName());
        assertEquals("North City", response2.getLocation());
        assertEquals("5", response2.getMemberCount());
        assertEquals("No Manager Assigned", response2.getManagerName());
        assertEquals("", response2.getManagerId());

        // Verify repository method calls
        verify(neighbourhoodRepository, times(1)).findAll();
        verify(userRepository, times(1)).countByNeighbourhoodId(1);
        verify(userRepository, times(1)).findManagerNameByNeighbourhoodId(1);
        verify(userRepository, times(1)).userRepositoryFindManagerIdByNeighbourhoodId(1);
        verify(userRepository, times(1)).countByNeighbourhoodId(2);
        verify(userRepository, times(1)).findManagerNameByNeighbourhoodId(2);
        verify(userRepository, times(1)).userRepositoryFindManagerIdByNeighbourhoodId(2);
    }
}
