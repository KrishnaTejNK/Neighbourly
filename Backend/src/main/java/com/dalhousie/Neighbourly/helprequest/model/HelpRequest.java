package com.dalhousie.Neighbourly.helprequest.model;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "help_requests")
public class HelpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User who is making the help request

    @ManyToOne
    @JoinColumn(name = "neighbourhood_id", nullable = false)
    private Neighbourhood neighbourhood;  // Neighbourhood the request is made for

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;  // 'Join' or 'Create' request type

    private String description;  // Description of the help request

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;  // 'Open', 'Approved', or 'Declined' status

    private LocalDateTime createdAt = LocalDateTime.now();  // Timestamp when the request was created

    private LocalDateTime updatedAt;  // Timestamp for the last update

    // Enum for RequestType (Join or Create)
    public enum RequestType {
        JOIN,
        CREATE
    }

    // Enum for RequestStatus (Open, Approved, Declined)
    public enum RequestStatus {
        OPEN,
        APPROVED,
        DECLINED
    }
}
