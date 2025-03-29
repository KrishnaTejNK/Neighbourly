package com.dalhousie.Neighbourly.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ParkingResponseDTO
 {
    private int requestId;
     private int rentalId;
     private int userId;
     private String status;
     private String name;
     private String spot;

     public ParkingResponseDTO(int requestId, int rentalId, int userId, String status, String name, String spot) {
         this.requestId = requestId;
         this.rentalId = rentalId;
         this.userId = userId;
         this.status = status;
         this.name = name;
         this.spot = spot;
     }
 }


