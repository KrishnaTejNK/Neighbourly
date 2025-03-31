package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;

import java.util.List;

/**
 * Service interface for managing neighbourhood-related operations.
 */
public interface NeighbourhoodService {

    /**
     * Retrieves all neighbourhoods with their details.
     * @return List of NeighbourhoodResponse objects containing neighbourhood information
     */
    List<NeighbourhoodResponse> getAllNeighbourhoods();
}