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

    private static final int TEST_NEIGHBOURHOOD_ID_1 = 1;
    private static final int TEST_NEIGHBOURHOOD_ID_2 = 2;
    private static final int TEST_AMENITY_ID = 1;

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
        Amenity mockAmenity = new Amenity();
        mockAmenity.setAmenityId(TEST_AMENITY_ID);
        mockAmenity.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_1);
        mockAmenity.setName("Pool");
        List<Amenity> mockAmenities = List.of(mockAmenity);

        when(amenityRepository.findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_1)).thenReturn(mockAmenities);

        // Act
        List<Amenity> result = amenityService.getAmenitiesByNeighbourhood(TEST_NEIGHBOURHOOD_ID_1);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return one amenity");
        assertEquals("Pool", result.get(0).getName(), "Amenity name should match");
        verify(amenityRepository, times(1)).findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_1);
    }

    @Test
    void getAmenitiesByNeighbourhood_returnsEmptyList_whenNoAmenitiesFound() {
        // Arrange
        when(amenityRepository.findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_2)).thenReturn(Collections.emptyList());

        // Act
        List<Amenity> result = amenityService.getAmenitiesByNeighbourhood(TEST_NEIGHBOURHOOD_ID_2);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return an empty list when no amenities are found");
        verify(amenityRepository, times(1)).findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_2);
    }

    @Test
    void createAmenity_savesAndReturnsAmenity() {
        // Arrange
        Amenity amenityToCreate = new Amenity();
        amenityToCreate.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_1);
        amenityToCreate.setName("Gym");

        Amenity savedAmenity = new Amenity();
        savedAmenity.setAmenityId(TEST_AMENITY_ID); // Simulate ID assigned by repository
        savedAmenity.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID_1);
        savedAmenity.setName("Gym");

        when(amenityRepository.save(any(Amenity.class))).thenReturn(savedAmenity);

        // Act
        Amenity result = amenityService.createAmenity(amenityToCreate);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(TEST_AMENITY_ID, result.getAmenityId(), "ID should be set after saving");
        assertEquals("Gym", result.getName(), "Amenity name should match");
        verify(amenityRepository, times(1)).save(amenityToCreate);
    }

    @Test
    void deleteAmenity_deletesById() {
        // Arrange
        // Act
        amenityService.deleteAmenity(TEST_AMENITY_ID);

        // Assert
        verify(amenityRepository, times(1)).deleteById(TEST_AMENITY_ID);
    }
}