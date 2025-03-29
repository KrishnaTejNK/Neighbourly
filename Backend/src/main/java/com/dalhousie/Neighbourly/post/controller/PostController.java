package com.dalhousie.Neighbourly.post.controller;

import com.dalhousie.Neighbourly.post.dto.PostRequest;
import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000") // Adjust based on frontend URL
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest) {
        String response = postService.createPost(postRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{neighbourhoodId}")
    public List<PostResponseDTO> getPosts(@PathVariable int neighbourhoodId) {
        return postService.getPostsByNeighbourhood(neighbourhoodId);
    }

    // Delete a post by postId
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable int postId) {
        boolean deleted = postService.deletePost(postId);
        if (deleted) {
            return ResponseEntity.ok("Post deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Error deleting post.");
        }
    }

}
