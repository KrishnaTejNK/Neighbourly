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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    private static final int TEST_AMENITY_ID_1 = 1;
    private static final int TEST_AMENITY_ID_2 = 2;
    private static final int TEST_NEIGHBOURHOOD_ID = 10;
    private static final String SWIMMING_POOL = "Swimming Pool";
    private static final String COMMUNITY_HALL = "Community Hall";
    private static final int EXPECTED_AMENITY_COUNT = 2;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityService amenityService;

    private Amenity amenity1, amenity2;

    @BeforeEach
    void setUp() {
        amenity1 = new Amenity();
        amenity1.setAmenityId(TEST_AMENITY_ID_1);
        amenity1.setName(SWIMMING_POOL);
        amenity1.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        amenity2 = new Amenity();
        amenity2.setAmenityId(TEST_AMENITY_ID_2);
        amenity2.setName(COMMUNITY_HALL);
        amenity2.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void testGetAmenitiesByNeighbourhood() {
        when(amenityRepository.findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(Arrays.asList(amenity1, amenity2));

        List<Amenity> amenities = amenityService.getAmenitiesByNeighbourhood(TEST_NEIGHBOURHOOD_ID);

        assertNotNull(amenities);
        assertEquals(EXPECTED_AMENITY_COUNT, amenities.size());
        assertEquals(SWIMMING_POOL, amenities.get(0).getName());
        assertEquals(COMMUNITY_HALL, amenities.get(1).getName());
        verify(amenityRepository, times(1)).findByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
    }

    @Test
    void testCreateAmenity() {
        when(amenityRepository.save(amenity1)).thenReturn(amenity1);

        Amenity createdAmenity = amenityService.createAmenity(amenity1);

        assertNotNull(createdAmenity);
        assertEquals(TEST_AMENITY_ID_1, createdAmenity.getAmenityId());
        assertEquals(SWIMMING_POOL, createdAmenity.getName());
        verify(amenityRepository, times(1)).save(amenity1);
    }

    @Test
    void testDeleteAmenity() {
        doNothing().when(amenityRepository).deleteById(TEST_AMENITY_ID_1);

        amenityService.deleteAmenity(TEST_AMENITY_ID_1);

        verify(amenityRepository, times(1)).deleteById(TEST_AMENITY_ID_1);
    }
}
