package com.dalhousie.Neighbourly.neighbourhood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NeighbourhoodResponse {
    private int neighbourhoodId;
    private String name;
    private String location;
    private String memberCount;
    private String managerName;
    private String managerId;
}
