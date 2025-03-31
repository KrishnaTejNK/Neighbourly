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
        testNeighbourhood.setNeighbourhoodId(1);
        testNeighbourhood.setName("Test Neighbourhood");
        testNeighbourhood.setLocation("Test Location");
    }

    @Test
    void getAllNeighbourhoods_withNeighbourhoods_returnsResponseList() {
        when(neighbourhoodRepository.findAll()).thenReturn(List.of(testNeighbourhood));
        when(userRepository.countByNeighbourhoodId(1)).thenReturn(5L);
        when(userRepository.findManagerNameByNeighbourhoodId(1)).thenReturn("Test Manager");
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(1)).thenReturn("manager1");

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals(1, response.getNeighbourhoodId());
        assertEquals("Test Neighbourhood", response.getName());
        assertEquals("Test Location", response.getLocation());
        assertEquals("5", response.getMemberCount());
        assertEquals("Test Manager", response.getManagerName());
        assertEquals("manager1", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(1);
        verify(userRepository).findManagerNameByNeighbourhoodId(1);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(1);
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
        when(userRepository.countByNeighbourhoodId(1)).thenReturn(0L);
        when(userRepository.findManagerNameByNeighbourhoodId(1)).thenReturn(null);
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(1)).thenReturn(null);

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals("0", response.getMemberCount());
        assertEquals("No Manager Assigned", response.getManagerName());
        assertEquals("", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(1);
        verify(userRepository).findManagerNameByNeighbourhoodId(1);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(1);
    }

    @Test
    void mapToNeighbourhoodResponse_blankManagerName_returnsDefaultName() {
        when(neighbourhoodRepository.findAll()).thenReturn(List.of(testNeighbourhood));
        when(userRepository.countByNeighbourhoodId(1)).thenReturn(3L);
        when(userRepository.findManagerNameByNeighbourhoodId(1)).thenReturn("");
        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(1)).thenReturn("manager1");

        List<NeighbourhoodResponse> result = neighbourhoodService.getAllNeighbourhoods();

        assertEquals(1, result.size());
        NeighbourhoodResponse response = result.get(0);
        assertEquals("3", response.getMemberCount());
        assertEquals("No Manager Assigned", response.getManagerName());
        assertEquals("manager1", response.getManagerId());
        verify(neighbourhoodRepository).findAll();
        verify(userRepository).countByNeighbourhoodId(1);
        verify(userRepository).findManagerNameByNeighbourhoodId(1);
        verify(userRepository).userRepositoryFindManagerIdByNeighbourhoodId(1);
    }
}