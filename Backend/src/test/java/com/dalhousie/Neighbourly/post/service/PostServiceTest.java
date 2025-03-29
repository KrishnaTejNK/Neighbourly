package com.dalhousie.Neighbourly.post.service;

import com.dalhousie.Neighbourly.neighbourhood.entity.Neighbourhood;
import com.dalhousie.Neighbourly.neighbourhood.repository.NeighbourhoodRepository;
import com.dalhousie.Neighbourly.post.dto.PostRequest;
import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.entity.Post;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.user.entity.User;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @InjectMocks
    private PostService postService;

    private User user;
    private Neighbourhood neighbourhood;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        neighbourhood = new Neighbourhood();
        neighbourhood.setNeighbourhoodId(1);
        neighbourhood.setName("Downtown");

        post = new Post();
        post.setPostId(1);
        post.setUser_id(user.getId());
        post.setNeighbourhood_id(neighbourhood.getNeighbourhoodId());
        post.setPostType("Help");
        post.setPostContent("Looking for help with groceries.");
    }

    @Test
    void createPost_UserNotFound() {
        PostRequest postRequest = new PostRequest();
        postRequest.setEmail("nonexistent@example.com");
        postRequest.setNeighbourhoodId(1);
        postRequest.setPostType("Help");
        postRequest.setPostContent("Looking for help with groceries.");

        String result = postService.createPost(postRequest);

        assertEquals("User not found!", result);
    }

    @Test
    void createPost_NeighbourhoodNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(anyInt())).thenReturn(Optional.empty());

        PostRequest postRequest = new PostRequest();
        postRequest.setEmail("john.doe@example.com");
        postRequest.setNeighbourhoodId(1);
        postRequest.setPostType("Help");
        postRequest.setPostContent("Looking for help with groceries.");

        String result = postService.createPost(postRequest);

        assertEquals("Neighbourhood not found!", result);
    }

    @Test
    void createPost_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(neighbourhoodRepository.findById(anyInt())).thenReturn(Optional.of(neighbourhood));

        PostRequest postRequest = new PostRequest();
        postRequest.setEmail("john.doe@example.com");
        postRequest.setNeighbourhoodId(1);
        postRequest.setPostType("Help");
        postRequest.setPostContent("Looking for help with groceries.");

        String result = postService.createPost(postRequest);

        assertEquals("Post created successfully!", result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPostsByNeighbourhood() {
        when(postRepository.findAllByNeighbourhoodId(anyInt())).thenReturn(List.of(post));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        List<PostResponseDTO> posts = postService.getPostsByNeighbourhood(1);

        assertEquals(1, posts.size());
        assertEquals("Looking for help with groceries.", posts.get(0).getPostContent());
        assertEquals("John Doe", posts.get(0).getUserName());
    }

    @Test
    void deletePost_PostNotFound() {
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean result = postService.deletePost(1);

        assertFalse(result);
        verify(postRepository, times(0)).deleteById(anyInt());
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

        boolean result = postService.deletePost(1);

        assertTrue(result);
        verify(postRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void getPostById_PostNotFound() {
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            postService.getPostById(1);
        });

        assertEquals("Post not found with ID: 1", exception.getMessage());
    }

    @Test
    void getPostById_Success() {
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        PostResponseDTO postResponseDTO = postService.getPostById(1);

        assertNotNull(postResponseDTO);
        assertEquals("Looking for help with groceries.", postResponseDTO.getPostContent());
        assertEquals("John Doe", postResponseDTO.getUserName());
    }
}
