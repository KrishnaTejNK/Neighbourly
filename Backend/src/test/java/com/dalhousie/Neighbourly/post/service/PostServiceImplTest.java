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

    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int TEST_POST_ID = 1;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

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
        testUser.setId(TEST_USER_ID);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        testNeighbourhood = new Neighbourhood();
        testNeighbourhood.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);

        testPost = new Post();
        testPost.setPostId(TEST_POST_ID);
        testPost.setUser_id(TEST_USER_ID);
        testPost.setNeighbourhood_id(TEST_NEIGHBOURHOOD_ID);
        testPost.setPostType("General");
        testPost.setPostContent("Test content");
        testPost.setDateTime(LocalDateTime.now());

        testPostRequest = new PostRequest();
        testPostRequest.setEmail("test@example.com");
        testPostRequest.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        testPostRequest.setPostContent("Test content");
        testPostRequest.setPostType("General");
    }

    @Test
    void createPost_successful_returnsSuccessMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.of(testNeighbourhood));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("Post created successfully!", result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
        verify(postRepository, times(EXPECTED_CALL_COUNT)).save(any(Post.class));
    }

    @Test
    void createPost_userNotFound_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("User not found!", result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, never()).findById(anyInt());
        verify(postRepository, never()).save(any());
    }

    @Test
    void createPost_neighbourhoodNotFound_returnsErrorMessage() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(neighbourhoodRepository.findById(TEST_NEIGHBOURHOOD_ID)).thenReturn(Optional.empty());

        // Act
        String result = postService.createPost(testPostRequest);

        // Assert
        assertEquals("Neighbourhood not found!", result);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findByEmail("test@example.com");
        verify(neighbourhoodRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_NEIGHBOURHOOD_ID);
        verify(postRepository, never()).save(any());
    }

    @Test
    void getPostsByNeighbourhood_returnsPostList() {
        // Arrange
        when(postRepository.findAllByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(List.of(testPost));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        // Act
        List<PostResponseDTO> result = postService.getPostsByNeighbourhood(TEST_NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_LIST_SIZE, result.size());
        PostResponseDTO dto = result.get(0);
        assertEquals(TEST_POST_ID, dto.getPostId());
        assertEquals(TEST_USER_ID, dto.getUserId());
        assertEquals("Test User", dto.getUserName());
        assertEquals("Test content", dto.getPostContent());
        assertEquals(testPost.getDateTime(), dto.getDateTime());
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findAllByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
    }

    @Test
    void getPostsByNeighbourhood_userNotFound_returnsUnknownUser() {
        // Arrange
        when(postRepository.findAllByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(List.of(testPost));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // Act
        List<PostResponseDTO> result = postService.getPostsByNeighbourhood(TEST_NEIGHBOURHOOD_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EXPECTED_LIST_SIZE, result.size());
        PostResponseDTO dto = result.get(0);
        assertEquals("Unknown User", dto.getUserName());
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findAllByNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
    }

    @Test
    void deletePost_postExists_returnsTrue() {
        // Arrange
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));

        // Act
        boolean result = postService.deletePost(TEST_POST_ID);

        // Assert
        assertTrue(result);
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_POST_ID);
        verify(postRepository, times(EXPECTED_CALL_COUNT)).deleteById(TEST_POST_ID);
    }

    @Test
    void deletePost_postNotFound_returnsFalse() {
        // Arrange
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.empty());

        // Act
        boolean result = postService.deletePost(TEST_POST_ID);

        // Assert
        assertFalse(result);
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_POST_ID);
        verify(postRepository, never()).deleteById(anyInt());
    }

    @Test
    void getPostById_postExists_returnsPostDTO() {
        // Arrange
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(testPost));
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));

        // Act
        PostResponseDTO result = postService.getPostById(TEST_POST_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_POST_ID, result.getPostId());
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals("Test User", result.getUserName());
        assertEquals("Test content", result.getPostContent());
        assertEquals(testPost.getDateTime(), result.getDateTime());
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_POST_ID);
        verify(userRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_USER_ID);
    }

    @Test
    void getPostById_postNotFound_throwsException() {
        // Arrange
        when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postService.getPostById(TEST_POST_ID));
        assertEquals("Post not found with ID: " + TEST_POST_ID, exception.getMessage());
        verify(postRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_POST_ID);
        verify(userRepository, never()).findById(anyInt());
    }
}