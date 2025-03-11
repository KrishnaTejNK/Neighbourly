package com.dalhousie.Neighbourly.post.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.post.dto.PostRequest;
import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.entity.Post;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NeighbourhoodRepository neighbourhoodRepository;

    public String createPost(PostRequest postRequest) {
        Optional<User> userOpt = userRepository.findByEmail(postRequest.getEmail());
        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        Optional<Neighbourhood> neighbourhoodOpt = neighbourhoodRepository.findById(postRequest.getNeighbourhoodId());
        if (neighbourhoodOpt.isEmpty()) {
            return "Neighbourhood not found!";
        }

        Post post = new Post();
        post.setUser_id(userOpt.get().getId());
        post.setNeighbourhood_id(neighbourhoodOpt.get().getNeighbourhoodId());
        post.setPostType(postRequest.getPostType());
        post.setPostContent(postRequest.getPostContent());

        postRepository.save(post);
        return "Post created successfully!";
    }


    public List<PostResponseDTO> getPostsByNeighbourhood(int neighbourhoodId) {
        List<Post> posts = postRepository.findAllByNeighbourhoodId(neighbourhoodId);

        return posts.stream().map(post -> {
            User user = userRepository.findById(post.getUser_id()).orElse(null);


            return new PostResponseDTO(
                    post.getPostId(),
                    post.getUser_id(),
                    user != null ? user.getName() : "Unknown User",
                    post.getPostContent(),
                    post.getDateTime()
            );
        }).collect(Collectors.toList());
    }

//    // Get all posts for a specific neighborhood
//    public List<Post> getPostsByNeighbourhood(int neighbourhoodId) {
//        return postRepository.findByNeighbourhoodId(neighbourhoodId);
//    }

    // Delete a post
    public boolean deletePost(int postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }

}
