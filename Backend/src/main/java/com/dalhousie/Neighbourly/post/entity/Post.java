package com.dalhousie.Neighbourly.post.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @Column(nullable = true)
    private int user_id;

    @Column(nullable = true)
    private Integer neighbourhood_id;  // Nullable if user hasn't joined a community

    @Column(name = "post_type", nullable = false)
    private String postType; // e.g., "Tools", "Emergency", "Event Support"

    @Column(name = "post_content", nullable = false, columnDefinition = "TEXT")
    private String postContent;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime = LocalDateTime.now();

    @Column(name = "approved", columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean approved = false; // By default, not approved
}
