package com.dalhousie.Neighbourly.post.service;

import com.dalhousie.Neighbourly.post.dto.PostRequest;
import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;

import java.util.List;

/**
 * Service interface for managing posts within neighbourhoods.
 */
public interface PostService {

    /**
     * Creates a new post based on the provided request.
     * @param postRequest The details of the post to create
     * @return A message indicating the result of the operation
     */
    String createPost(PostRequest postRequest);

    /**
     * Retrieves all posts for a given neighbourhood.
     * @param neighbourhoodId The ID of the neighbourhood
     * @return List of PostResponseDTO objects representing the posts
     */
    List<PostResponseDTO> getPostsByNeighbourhood(int neighbourhoodId);

    /**
     * Deletes a post by its ID.
     * @param postId The ID of the post to delete
     * @return True if the post was deleted, false if it wasn’t found
     */
    boolean deletePost(int postId);

    /**
     * Retrieves a single post by its ID.
     * @param postId The ID of the post to retrieve
     * @return PostResponseDTO representing the post
     * @throws RuntimeException if the post is not found
     */
    PostResponseDTO getPostById(int postId);
}