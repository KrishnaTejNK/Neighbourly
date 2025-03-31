package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AmenityServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    @BeforeEach
    void setUp() {
        // Any setup can go here if needed (e.g., resetting mocks), but not required for this case
    }

    @Test
    void getAmenitiesByNeighbourhood_returnsAmenities() {
        // Arrange
        int neighbourhoodId = 1;
        Amenity mockAmenity = new Amenity();
        mockAmenity.setAmenityId(1);
        mockAmenity.setNeighbourhoodId(neighbourhoodId);
        mockAmenity.setName("Pool");
        List<Amenity> mockAmenities = List.of(mockAmenity);

        when(amenityRepository.findByNeighbourhoodId(neighbourhoodId)).thenReturn(mockAmenities);

        // Act
        List<Amenity> result = amenityService.getAmenitiesByNeighbourhood(neighbourhoodId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one amenity");
        assertEquals("Pool", result.get(0).getName(), "Amenity name should match");
        verify(amenityRepository, times(1)).findByNeighbourhoodId(neighbourhoodId);
    }

    @Test
    void getAmenitiesByNeighbourhood_returnsEmptyList_whenNoAmenitiesFound() {
        // Arrange
        int neighbourhoodId = 2;
        when(amenityRepository.findByNeighbourhoodId(neighbourhoodId)).thenReturn(Collections.emptyList());

        // Act
        List<Amenity> result = amenityService.getAmenitiesByNeighbourhood(neighbourhoodId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return an empty list when no amenities are found");
        verify(amenityRepository, times(1)).findByNeighbourhoodId(neighbourhoodId);
    }

    @Test
    void createAmenity_savesAndReturnsAmenity() {
        // Arrange
        Amenity amenityToCreate = new Amenity();
        amenityToCreate.setNeighbourhoodId(1);
        amenityToCreate.setName("Gym");

        Amenity savedAmenity = new Amenity();
        savedAmenity.setAmenityId(1); // Simulate ID assigned by repository
        savedAmenity.setNeighbourhoodId(1);
        savedAmenity.setName("Gym");

        when(amenityRepository.save(any(Amenity.class))).thenReturn(savedAmenity);

        // Act
        Amenity result = amenityService.createAmenity(amenityToCreate);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getAmenityId(), "ID should be set after saving");
        assertEquals("Gym", result.getName(), "Amenity name should match");
        verify(amenityRepository, times(1)).save(amenityToCreate);
    }

    @Test
    void deleteAmenity_deletesById() {
        // Arrange
        int amenityId = 1;

        // Act
        amenityService.deleteAmenity(amenityId);

        // Assert
        verify(amenityRepository, times(1)).deleteById(amenityId);
    }
}