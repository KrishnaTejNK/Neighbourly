package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NeighbourhoodServiceImplTest {

    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int MOCK_TEST_NEIGHBOURHOOD_ID = 3;
    private static final int MOCK_TEST_NEIGHBOURHOOD_ID2 = 5;
    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NeighbourhoodServiceImpl neighbourhoodService;

    private Neighbourhood testNeighbourhood;

    @BeforeEach
    void setUp() {
        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        testNeighbourhood.setName("Test Neighbourhood");
        testNeighbourhood.setLocation("Test Location");
    }

    @Test
    void getAllNeighbourhoods_withNeighbourhoods_returnsResponseList() {


        when(neighbourhoodRepository.findAll()).thenReturn(List.of(testNeighbourhood));
        when(userRepository.countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn((long) MOCK_TEST_NEIGHBOURHOOD_ID2);
        when(userRepository.findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn("Test Manager");
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn("manager1");

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals(TEST_NEIGHBOURHOOD_ID, response.getNeighbourhoodId());
        assertEquals("Test Neighbourhood", response.getName());
        assertEquals("Test Location", response.getLocation());
        assertEquals("5", response.getMemberCount());
        assertEquals("Test Manager", response.getManagerName());
        assertEquals("manager1", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void getAllNeighbourhoods_noNeighbourhoods_returnsEmptyList() {
        when(neighbourhoodRepository.findAll()).thenReturn(Collections.emptyList());

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertTrue(result.isEmpty());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository, never()).countByNeighbourhoodId(anyInt());
        verify(userRepository, never()).findManagerNameByNeighbourhoodId(anyInt());
        verify(userRepository, never()).userRepositoryFindManagerIdByNeighbourhoodId(anyInt());
    }

    @Test
    void mapToNeighbourhoodResponse_noManager_returnsDefaultValues() {
        when(neighbourhoodRepository.findAll()).thenReturn(List.of(testNeighbourhood));
        when(userRepository.countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(0L);
        when(userRepository.findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(null);
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(null);

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals("0", response.getMemberCount());
        assertEquals("No Manager Assigned", response.getManagerName());
        assertEquals("", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void mapToNeighbourhoodResponse_blankManagerName_returnsDefaultName() {

        when(neighbourhoodRepository.findAll()).thenReturn(List.of(testNeighbourhood));
        when(userRepository.countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn((long) MOCK_TEST_NEIGHBOURHOOD_ID);
        when(userRepository.findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn("");
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn("manager1");

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals("3", response.getMemberCount());
        assertEquals("No Manager Assigned", response.getManagerName());
        assertEquals("manager1", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).findManagerNameByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }
}