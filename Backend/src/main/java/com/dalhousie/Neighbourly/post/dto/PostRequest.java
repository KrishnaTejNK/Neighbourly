package com.dalhousie.Neighbourly.post.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String email; // User's email (to fetch user_id)
    private int neighbourhoodId;
    private String postType;
    private String postContent;
}
