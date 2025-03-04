package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import com.dalhousie.Neighbourly.user.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class NeighbourhoodServiceTest {

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NeighbourhoodService neighbourhoodService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllNeighbourhoods() {
//        // Given
//        Neighbourhood neighbourhood1 = new Neighbourhood();
//        neighbourhood1.setNeighbourhoodId(1);
//        neighbourhood1.setName("Community A");
//        neighbourhood1.setLocation("Location A");
//
//        Neighbourhood neighbourhood2 = new Neighbourhood();
//        neighbourhood2.setNeighbourhoodId(2);
//        neighbourhood2.setName("Community B");
//        neighbourhood2.setLocation("Location B");
//
//        User manager = new User();
//        manager.setName("John Doe");
//        manager.setUserType(UserType.COMMUNITY_MANAGER);
//        manager.setNeighbourhood_id(1);
//
//        when(neighbourhoodRepository.findAll()).thenReturn(Arrays.asList(neighbourhood1, neighbourhood2));
//        when(userRepository.countByNeighbourhoodId(1)).thenReturn(10L);
//        when(userRepository.userRepositoryFindManagerIdByNeighbourhoodId(1)).thenReturn(String.valueOf(Optional.of(manager)));
//
//        // When
//        List<NeighbourhoodResponse> responses = neighbourhoodService.getAllNeighbourhoods();
//
//        // Then
//        assertEquals(2, responses.size());
//        assertEquals("Community A", responses.get(0).getName());
//        assertEquals("10", responses.get(0).getMemberCount());
//        assertEquals("John Doe", responses.get(0).getManagerName());
//
//        verify(neighbourhoodRepository, times(1)).findAll();
//        verify(userRepository, times(1)).countByNeighbourhoodId(1);
//        verify(userRepository, times(1)).userRepositoryFindManagerIdByNeighbourhoodId(1);
    }
}
