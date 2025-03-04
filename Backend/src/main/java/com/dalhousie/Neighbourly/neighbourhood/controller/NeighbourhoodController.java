package com.dalhousie.Neighbourly.neighbourhood.controller;

import com.dalhousie.Neighbourly.neighbourhood.dto.NeighbourhoodResponse;
import com.dalhousie.Neighbourly.neighbourhood.service.NeighbourhoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/neighbourhoods")
public class NeighbourhoodController {

    @Autowired
    private NeighbourhoodService neighbourhoodService;

    @GetMapping("/getallneighbourhoods")
    public ResponseEntity<List<NeighbourhoodResponse>> getAllNeighbourhoods() {
        List<NeighbourhoodResponse> neighbourhoods = neighbourhoodService.getAllNeighbourhoods();
        return ResponseEntity.ok(neighbourhoods);
    }
}
