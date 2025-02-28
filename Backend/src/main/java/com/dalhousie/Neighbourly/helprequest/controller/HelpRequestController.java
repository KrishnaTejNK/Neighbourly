package com.dalhousie.Neighbourly.helprequest.controller;

import com.dalhousie.Neighbourly.helprequest.dto.HelpRequestDTO;
import com.dalhousie.Neighbourly.helprequest.model.HelpRequest;
import com.dalhousie.Neighbourly.helprequest.service.HelpRequestService;
import com.dalhousie.Neighbourly.util.CustomResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/help-requests")
@RequiredArgsConstructor
@Slf4j
public class HelpRequestController {

    private final HelpRequestService helpRequestService;

    @GetMapping("/{neighbourhoodId}")
    public ResponseEntity<List<HelpRequest>> getJoinRequests(@PathVariable int neighbourhoodId) {
        log.info("Entered getJoinRequests");
        List<HelpRequest> requests = helpRequestService.getAllJoinCommunityRequests(neighbourhoodId);
        return ResponseEntity.ok(requests);
    }

@GetMapping("/openCommunityRequests")
public ResponseEntity<CustomResponseBody<List<HelpRequestDTO>>> getOpenCommunityRequests() {
    try {
        log.info("Fetching all open community requests");
        List<HelpRequestDTO> requests = helpRequestService.getAllOpenCommunityRequests();

        if (requests.isEmpty()) {
            log.info("No open community requests found");
            CustomResponseBody<List<HelpRequestDTO>> responseBody =
                    new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, requests, "No open requests available");
            return ResponseEntity.status(HttpStatus.OK).body(responseBody);
        }

        CustomResponseBody<List<HelpRequestDTO>> responseBody =
                new CustomResponseBody<>(CustomResponseBody.Result.SUCCESS, requests, "Open community requests retrieved successfully");
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);

    } catch (Exception e) {
        log.error("Error fetching open community requests: {}", e.getMessage());
        CustomResponseBody<List<HelpRequestDTO>> responseBody =
                new CustomResponseBody<>(CustomResponseBody.Result.FAILURE, null, "Something went wrong while fetching open requests");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}



}
