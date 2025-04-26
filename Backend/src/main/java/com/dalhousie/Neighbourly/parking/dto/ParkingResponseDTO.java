package com.dalhousie.Neighbourly.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor

public class ParkingResponseDTO
{
private int requestId;
 private int rentalId;
 private int userId;
 private String status;
 private String name;
 private String spot;


}


