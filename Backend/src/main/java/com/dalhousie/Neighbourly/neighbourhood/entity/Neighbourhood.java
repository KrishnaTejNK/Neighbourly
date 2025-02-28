package com.dalhousie.Neighbourly.neighbourhood.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "neighbourhood")
public class Neighbourhood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "neighbourhood_id")
    private int neighbourhoodId;

    @Column(nullable = false)
    private String name;

    private String location;

}

