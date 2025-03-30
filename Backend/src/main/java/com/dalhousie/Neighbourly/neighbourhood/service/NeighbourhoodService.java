package com.dalhousie.Neighbourly.neighbourhood.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NeighbourhoodService {

    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    @Autowired
    private UserRepository userRepository;

    public List<NeighbourhoodResponse> getAllNeighbourhoods() {
        List<Neighbourhood> neighbourhoods = neighbourhoodRepository.findAll();

        return neighbourhoods.stream().map(this::mapToNeighbourhoodResponse).collect(Collectors.toList());
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
