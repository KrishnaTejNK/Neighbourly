package com.dalhousie.Neighbourly.amenity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenity_id")
    private int amenityId;

    @Column(name = "neighbourhood_id")
    private int neighbourhoodId;

    @Column(name = "name")
    private String name;

    @Column(name = "available_from")
    private Timestamp availableFrom;

    @Column(name = "available_to")
    private Timestamp availableTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.AVAILABLE;

    public Amenity() {
    }
    public Amenity(int amenityId, int neighbourhoodId, String name, Timestamp availableFrom, Timestamp availableTo, Status status) {
        this.amenityId = amenityId;
        this.neighbourhoodId = neighbourhoodId;
        this.name = name;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.status = status;
    }



}