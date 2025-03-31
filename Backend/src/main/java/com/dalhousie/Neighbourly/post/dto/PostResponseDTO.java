package com.dalhousie.Neighbourly.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private int postId;
    private int userId;
    private String userName;
    private String postContent;
    private LocalDateTime dateTime;

    public PostResponseDTO(int postId, String postContent) {
    }
}
