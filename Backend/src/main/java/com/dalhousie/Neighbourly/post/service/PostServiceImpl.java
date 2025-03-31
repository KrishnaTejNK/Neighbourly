package com.dalhousie.Neighbourly.post.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.post.dto.PostRequest;
import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.entity.Post;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of PostService for handling post-related operations.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;

    @Override
    public String createPost(PostRequest postRequest) {
        Optional<User> userOpt = userRepository.findByEmail(postRequest.getEmail());
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        Optional<Neighbourhood> neighbourhoodOpt = neighbourhoodRepository.findById(postRequest.getNeighbourhoodId());
        if (neighbourhoodOpt.isEmpty()) {
            return "Neighbourhood not found!";
        }

        Post post = buildPost(postRequest, userOpt.get(), neighbourhoodOpt.get());
        postRepository.save(post);
        return "Post created successfully!";
    }

    private Post buildPost(PostRequest postRequest, User user, Neighbourhood neighbourhood) {
        Post post = new Post();
        post.setUser_id(user.getId());
        post.setNeighbourhood_id(neighbourhood.getNeighbourhoodId());
        post.setPostType(postRequest.getPostType());
        post.setPostContent(postRequest.getPostContent());
        return post;
    }

    @Override
    public List<PostResponseDTO> getPostsByNeighbourhood(int neighbourhoodId) {
        List<Post> posts = postRepository.findAllByNeighbourhoodId(neighbourhoodId);
        return posts.stream()
                .map(this::mapPostToPostResponseDTO)
                .collect(Collectors.toList());
    }

    private PostResponseDTO mapPostToPostResponseDTO(Post post) {
        User user = userRepository.findById(post.getUser_id()).orElse(null);
        return new PostResponseDTO(
                post.getPostId(),
                post.getUser_id(),
                user != null ? user.getName() : "Unknown User",
                post.getPostContent(),
                post.getDateTime()
        );
    }

    @Override
    public boolean deletePost(int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }

    @Override
    public PostResponseDTO getPostById(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        User user = userRepository.findById(post.getUser_id()).orElse(null);

        return new PostResponseDTO(
                post.getPostId(),
                post.getUser_id(),
                user != null ? user.getName() : "Unknown User",
                post.getPostContent(),
                post.getDateTime()
        );
    }
}