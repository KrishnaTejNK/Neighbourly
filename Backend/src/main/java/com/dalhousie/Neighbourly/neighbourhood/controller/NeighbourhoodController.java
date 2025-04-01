package com.dalhousie.Neighbourly.neighbourhood.controller;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.service.NeighbourhoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/neighbourhoods")
@RequiredArgsConstructor
@CrossOrigin(origins = "origins = \"http://172.17.2.103:3080/\"")
public class NeighbourhoodController {

    private final NeighbourhoodService neighbourhoodService;

    @GetMapping("/getallneighbourhoods")
    public ResponseEntity<List<NeighbourhoodResponse>> getAllNeighbourhoods() {
        List<NeighbourhoodResponse> neighbourhoods = neighbourhoodService.getAllNeighbourhoods();
        return ResponseEntity.ok(neighbourhoods);
    }
}