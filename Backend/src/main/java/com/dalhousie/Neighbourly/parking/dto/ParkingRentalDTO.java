package com.dalhousie.Neighbourly.parking.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRentalDTO {
    private int neighbourhoodId;
    private int userId;
    private String spot;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;

}
