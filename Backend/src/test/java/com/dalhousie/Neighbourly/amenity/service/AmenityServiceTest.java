package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.entity.Amenity.Status;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityService amenityService;

    private Amenity amenity1;
    private Amenity amenity2;

    @BeforeEach
    void setUp() {
        Timestamp availableFrom1 = Timestamp.valueOf("2025-03-12 09:00:00");
        Timestamp availableTo1 = Timestamp.valueOf("2025-03-12 18:00:00");

        Timestamp availableFrom2 = Timestamp.valueOf("2025-03-12 06:00:00");
        Timestamp availableTo2 = Timestamp.valueOf("2025-03-12 22:00:00");

        amenity1 = new Amenity(1, 101, "Swimming Pool", availableFrom1, availableTo1, Status.AVAILABLE);
        amenity2 = new Amenity(2, 101, "Gym", availableFrom2, availableTo2, Status.BOOKED);
    }

    @Test
    void testGetAmenitiesByNeighbourhood() {
        // Arrange
        when(amenityRepository.findByNeighbourhoodId(101)).thenReturn(Arrays.asList(amenity1, amenity2));

        // Act
        List<Amenity> amenities = amenityService.getAmenitiesByNeighbourhood(101);

        // Assert
        assertNotNull(amenities);
        assertEquals(2, amenities.size());
        assertEquals("Swimming Pool", amenities.get(0).getName());
        assertEquals(Status.AVAILABLE, amenities.get(0).getStatus());
        verify(amenityRepository, times(1)).findByNeighbourhoodId(101);
    }

    @Test
    void testCreateAmenity() {
        // Arrange
        when(amenityRepository.save(amenity1)).thenReturn(amenity1);

        // Act
        Amenity savedAmenity = amenityService.createAmenity(amenity1);

        // Assert
        assertNotNull(savedAmenity);
        assertEquals("Swimming Pool", savedAmenity.getName());
        assertEquals(Status.AVAILABLE, savedAmenity.getStatus());
        verify(amenityRepository, times(1)).save(amenity1);
    }

    @Test
    void testDeleteAmenity() {
        // Arrange
        doNothing().when(amenityRepository).deleteById(1);

        // Act
        amenityService.deleteAmenity(1);

        // Assert
        verify(amenityRepository, times(1)).deleteById(1);
    }
}
