package com.dalhousie.Neighbourly.helprequest.repository;

import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HelpRequestRepository extends JpaRepository<HelpRequest, Integer> {


    // Get all requests for a neighbourhood
    List<HelpRequest> findByNeighbourhood(Neighbourhood neighbourhood);

    // Get only JOIN requests for a neighbourhood
    List<HelpRequest> findByNeighbourhoodAndRequestType(Neighbourhood neighbourhood, HelpRequest.RequestType requestType);

    // Get only JOIN requests with status OPEN for a neighbourhood (NEW METHOD)
    List<HelpRequest> findByNeighbourhoodAndRequestTypeAndStatus(
            Neighbourhood neighbourhood, HelpRequest.RequestType requestType, HelpRequest.RequestStatus status);

    // Get all requests with status OPEN for community creation/neighbourhood
    List<HelpRequest> findByStatus(HelpRequest.RequestStatus status);
    Optional<HelpRequest> findByRequestId(int requestId);
}
