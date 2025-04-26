package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of NeighbourhoodService for handling neighbourhood-related operations.
 */
@Service
@RequiredArgsConstructor
public class NeighbourhoodServiceImpl implements NeighbourhoodService {

    private final NeighbourhoodRepository neighbourhoodRepository;
    private final UserRepository userRepository;

    @Override
    public List<NeighbourhoodResponse> getAllNeighbourhoods() {
        List<Neighbourhood> neighbourhoods = neighbourhoodRepository.findAll();
        return neighbourhoods.stream()
                .map(this::mapToNeighbourhoodResponse)
                .collect(Collectors.toList());
    }

    private NeighbourhoodResponse mapToNeighbourhoodResponse(Neighbourhood neighbourhood) {
        long memberCount = userRepository.countByNeighbourhoodId(neighbourhood.getNeighbourhoodId());
        String managerName = userRepository.findManagerNameByNeighbourhoodId(neighbourhood.getNeighbourhoodId());
        String managerId = userRepository.userRepositoryFindManagerIdByNeighbourhoodId(neighbourhood.getNeighbourhoodId());

        return new NeighbourhoodResponse(
                neighbourhood.getNeighbourhoodId(),
                neighbourhood.getName(),
                neighbourhood.getLocation(),
                String.valueOf(memberCount), // Convert long to String
                StringUtils.defaultIfBlank(managerName, "No Manager Assigned"),
                StringUtils.defaultIfBlank(managerId, "")
        );
    }
}