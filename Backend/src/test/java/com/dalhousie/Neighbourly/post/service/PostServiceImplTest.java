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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NeighbourhoodRepository neighbourhoodRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User testUser;
    private Neighbourhood testNeighbourhood;
    private Post testPost;
    private PostRequest testPostRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(1);

        testPost = new Post();
        testPost.setPostId(1);
        testPost.setUser_id(1);
        testPost.setNeighbourhood_id(1);
        testPost.setPostType("General");
        testPost.setPostContent("Test content");
        testPost.setDateTime(LocalDateTime.now());

        testPostRequest = new PostRequest();
        testPostRequest.setEmail("test@example.com");
        testPostRequest.setNeighbourhoodId(1);
        testPostRequest.setPostContent("Test content");
        testPostRequest.setPostType("General");
    }

    @Test
    void createPost_successful_returnsSuccessMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(1)).thenReturn(Optional.of(testNeighbourhood));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("Post created successfully!", result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, times(1)).findById(1);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void createPost_userNotFound_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("User not found!", result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, never()).findById(anyInt());
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_neighbourhoodNotFound_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("Neighbourhood not found!", result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, times(1)).findById(1);
        verify(postRepository, never()).save(any());
    }

    @Test
    void getPostsByNeighbourhood_returnsPostList() {
        // Arrange
        when(postRepository.findAllByNeighbourhoodId(1)).thenReturn(List.of(testPost));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        List<PostResponseDTO> result = postService.getPostsByNeighbourhood(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        PostResponseDTO dto = result.get(0);
        assertEquals(1, dto.getPostId());
        assertEquals(1, dto.getUserId());
        assertEquals("Test User", dto.getUserName());
        assertEquals("Test content", dto.getPostContent());
        assertEquals(testPost.getDateTime(), dto.getDateTime());
        verify(postRepository, times(1)).findAllByNeighbourhoodId(1);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getPostsByNeighbourhood_userNotFound_returnsUnknownUser() {
        // Arrange
        when(postRepository.findAllByNeighbourhoodId(1)).thenReturn(List.of(testPost));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        List<PostResponseDTO> result = postService.getPostsByNeighbourhood(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        PostResponseDTO dto = result.get(0);
        assertEquals("Unknown User", dto.getUserName());
        verify(postRepository, times(1)).findAllByNeighbourhoodId(1);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void deletePost_postExists_returnsTrue() {
        // Arrange
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));

        // Act
        boolean result = postService.deletePost(1);

        // Assert
        assertTrue(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, times(1)).deleteById(1);
    }

    @Test
    void deletePost_postNotFound_returnsFalse() {
        // Arrange
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        boolean result = postService.deletePost(1);

        // Assert
        assertFalse(result);
        verify(postRepository, times(1)).findById(1);
        verify(postRepository, never()).deleteById(anyInt());
    }

    @Test
    void getPostById_postExists_returnsPostDTO() {
        // Arrange
        when(postRepository.findById(1)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        PostResponseDTO result = postService.getPostById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getPostId());
        assertEquals(1, result.getUserId());
        assertEquals("Test User", result.getUserName());
        assertEquals("Test content", result.getPostContent());
        assertEquals(testPost.getDateTime(), result.getDateTime());
        verify(postRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void getPostById_postNotFound_throwsException() {
        // Arrange
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.getPostById(1));
        assertEquals("Post not found with ID: 1", exception.getMessage());
        verify(postRepository, times(1)).findById(1);
        verify(userRepository, never()).findById(anyInt());
    }
}