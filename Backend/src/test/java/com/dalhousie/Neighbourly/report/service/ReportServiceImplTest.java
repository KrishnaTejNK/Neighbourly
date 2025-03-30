package com.dalhousie.Neighbourly.report.service;

import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.entity.Post;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.post.service.PostService;
import com.dalhousie.Neighbourly.report.dto.ReportDTO;
import com.dalhousie.Neighbourly.report.entity.Report;
import com.dalhousie.Neighbourly.report.repository.ReportRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    private static final int SAMPLE_USER_ID = 1;
    private static final int SAMPLE_POST_ID = 100;
    private static final int SAMPLE_NEIGHBOURHOOD_ID = 50;
    private static final int SAMPLE_REPORT_ID = 1;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private Report report;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(SAMPLE_USER_ID);
        user.setName("John Doe");

        post = new Post();
        post.setPostId(SAMPLE_POST_ID);
        post.setUser_id(user.getId());
        post.setPostContent("Test post content");

        report = new Report();
        report.setReportid(SAMPLE_REPORT_ID);
        report.setUserid(user.getId());
        report.setPostid(post.getPostId());
        report.setNeighbourhoodid(SAMPLE_NEIGHBOURHOOD_ID);
        report.setReportedAt(LocalDateTime.now());
        report.setReportStatus(Report.ReportStatus.PENDING);
    }

    @Test
    void testReportPost() {
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        assertDoesNotThrow(() -> reportService.reportPost(SAMPLE_NEIGHBOURHOOD_ID, SAMPLE_POST_ID, SAMPLE_USER_ID));

        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testGetReportedPosts() {
        when(reportRepository.findByPost_NeighbourhoodId(SAMPLE_NEIGHBOURHOOD_ID)).thenReturn(List.of(report));

        // Create mock PostResponseDTO
        PostResponseDTO postResponseDTO = new PostResponseDTO(post.getPostId(), post.getPostContent());
        when(postService.getPostById(report.getPostid())).thenReturn(postResponseDTO);

        List<ReportDTO> reportDTOS = reportService.getReportedPosts(SAMPLE_NEIGHBOURHOOD_ID);

        assertFalse(reportDTOS.isEmpty());
        assertEquals(1, reportDTOS.size());
        assertEquals(post.getPostId(), reportDTOS.get(0).getPostId());
        verify(reportRepository, times(1)).findByPost_NeighbourhoodId(SAMPLE_NEIGHBOURHOOD_ID);
        verify(postService, times(1)).getPostById(report.getPostid());
    }

    @Test
    void testApprovePost() {
        when(reportRepository.findById(SAMPLE_REPORT_ID)).thenReturn(Optional.of(report));

        assertDoesNotThrow(() -> reportService.approvePost(SAMPLE_REPORT_ID));

        assertEquals(Report.ReportStatus.REVIEWED, report.getReportStatus());
        verify(reportRepository, times(1)).save(report);
    }

    @Test
    void testDeletePost() {
        when(reportRepository.findById(SAMPLE_REPORT_ID)).thenReturn(Optional.of(report));

        assertDoesNotThrow(() -> reportService.deletePost(SAMPLE_REPORT_ID));

        assertEquals(Report.ReportStatus.RESOLVED, report.getReportStatus());
        verify(postRepository, times(1)).deleteById(report.getPostid());
    }
}
