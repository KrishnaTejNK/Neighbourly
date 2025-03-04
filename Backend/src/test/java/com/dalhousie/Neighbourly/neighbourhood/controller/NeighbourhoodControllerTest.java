package com.dalhousie.Neighbourly.neighbourhood.controller;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.service.NeighbourhoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class NeighbourhoodControllerTest {

    @Mock
    private NeighbourhoodService neighbourhoodService;

    @InjectMocks
    private NeighbourhoodController neighbourhoodController;

    @Test
    public void testGetAllNeighbourhoods() {

        // Given
        NeighbourhoodResponse response1 = new NeighbourhoodResponse(1, "Community A", "Location A", "10", "John Doe","1");
        NeighbourhoodResponse response2 = new NeighbourhoodResponse(2, "Community B", "Location B", "15", "Jane Doe","2");

        when(neighbourhoodService.getAllNeighbourhoods()).thenReturn(Arrays.asList(response1, response2));

        // When
        ResponseEntity<List<NeighbourhoodResponse>> response = neighbourhoodController.getAllNeighbourhoods();

        // Then
        assertEquals(OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Community A", response.getBody().get(0).getName());

        verify(neighbourhoodService, times(1)).getAllNeighbourhoods();

    }
}
