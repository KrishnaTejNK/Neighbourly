package com.dalhousie.Neighbourly.parking.dto;

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

     public ParkingResponseDTO(int requestId, int rentalId, int userId, String status) {
         this.requestId = requestId;
         this.rentalId = rentalId;
         this.userId = userId;
         this.status = status;
     }
 }


