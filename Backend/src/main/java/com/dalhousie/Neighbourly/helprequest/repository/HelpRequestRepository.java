package com.dalhousie.Neighbourly.helprequest.repository;

import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.model.RequestStatus;
import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HelpRequestRepository extends JpaRepository<HelpRequest, Integer> {


    // Get only JOIN requests with status OPEN for a neighbourhood (NEW METHOD)
    List<HelpRequest> findByNeighbourhoodAndRequestTypeAndStatus(
            Neighbourhood neighbourhood, HelpRequest.RequestType requestType, RequestStatus status);

    Optional<HelpRequest> findByRequestId(int requestId);

    List<HelpRequest> findByStatusAndRequestType(RequestStatus status, HelpRequest.RequestType requestType);
}
