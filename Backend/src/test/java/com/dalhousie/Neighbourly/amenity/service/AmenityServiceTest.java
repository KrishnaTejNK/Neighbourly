package com.dalhousie.Neighbourly.amenity.service;

import com.dalhousie.Neighbourly.amenity.entity.Amenity;
import com.dalhousie.Neighbourly.amenity.repository.AmenityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityService amenityService;

    private Amenity amenity1, amenity2;

    @BeforeEach
    void setUp() {
        amenity1 = new Amenity();
        amenity1.setAmenityId(1);
        amenity1.setName("Swimming Pool");
        amenity1.setNeighbourhoodId(10);

        amenity2 = new Amenity();
        amenity2.setAmenityId(2);
        amenity2.setName("Community Hall");
        amenity2.setNeighbourhoodId(10);
    }
    @Test
    void testGetAmenitiesByNeighbourhood() {
        when(amenityRepository.findByNeighbourhoodId(10)).thenReturn(Arrays.asList(amenity1, amenity2));

        List<Amenity> amenities = amenityService.getAmenitiesByNeighbourhood(10);

        assertNotNull(amenities);
        assertEquals(2, amenities.size());
        assertEquals("Swimming Pool", amenities.get(0).getName());
        assertEquals("Community Hall", amenities.get(1).getName());
        verify(amenityRepository, times(1)).findByNeighbourhoodId(10);
    }
    @Test
    void testCreateAmenity() {
        when(amenityRepository.save(amenity1)).thenReturn(amenity1);

        Amenity createdAmenity = amenityService.createAmenity(amenity1);

        assertNotNull(createdAmenity);
        assertEquals(amenity1.getAmenityId(), createdAmenity.getAmenityId());
        assertEquals("Swimming Pool", createdAmenity.getName());
        verify(amenityRepository, times(1)).save(amenity1);
    }
    @Test
    void testDeleteAmenity() {
        doNothing().when(amenityRepository).deleteById(1);

        amenityService.deleteAmenity(1);

        verify(amenityRepository, times(1)).deleteById(1);
    }
}
