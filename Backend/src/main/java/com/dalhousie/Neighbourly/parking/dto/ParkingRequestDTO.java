package com.dalhousie.Neighbourly.parking.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingRequestDTO{

    private int rentalId;
    private int userId;
    private String status;

    public ParkingRequestDTO( int rentalId, int userId, String status) {

        this.rentalId = rentalId;
        this.userId = userId;
        this.status = status;
    }

    public ParkingRequestDTO() {

    }


    // Getters and Setters
}
