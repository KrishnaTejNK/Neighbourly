package com.dalhousie.Neighbourly.report.service;

import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.post.service.PostService;
import com.dalhousie.Neighbourly.report.dto.ReportDTO;
import com.dalhousie.Neighbourly.report.entity.Report;
import com.dalhousie.Neighbourly.report.entity.ReportStatus;
import com.dalhousie.Neighbourly.report.repository.ReportRepository;
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

    private Report testReport;
    private PostResponseDTO testPostResponse;

    @BeforeEach
    void setUp() {
        testReport = new Report(1, 1, 1, ReportStatus.PENDING);
        testReport.setReportid(1);
        testReport.setReportedAt(LocalDateTime.now());

        testPostResponse = new PostResponseDTO();
        testPostResponse.setPostId(1);
        testPostResponse.setUserId(1);
    }

    @Test
    void reportPost_savesReportSuccessfully() {
        reportService.reportPost(1, 1, 1);

        verify(reportRepository).save(argThat(report ->
                report.getNeighbourhoodid() == 1 &&
                        report.getPostid() == 1 &&
                        report.getUserid() == 1 &&
                        report.getReportStatus() == ReportStatus.PENDING
        ));
    }

    @Test
    void getReportedPosts_returnsReportDTOList() {
        List<Report> reports = List.of(testReport);
        when(reportRepository.findByPost_NeighbourhoodId(1)).thenReturn(reports);
        when(postService.getPostById(1)).thenReturn(testPostResponse);

        List<ReportDTO> result = reportService.getReportedPosts(1);

        assertEquals(1, result.size());
        ReportDTO reportDTO = result.get(0);
        assertEquals(1, reportDTO.getId());
        assertEquals(1, reportDTO.getPostId());
        assertEquals(1, reportDTO.getUserId());
        assertEquals(1, reportDTO.getNeighbourhoodId());
        assertEquals(ReportStatus.PENDING, reportDTO.getReportStatus());
        verify(reportRepository).findByPost_NeighbourhoodId(1);
        verify(postService).getPostById(1);
    }

    @Test
    void getReportedPosts_postNotFound_returnsEmptyPostDetails() {
        List<Report> reports = List.of(testReport);
        when(reportRepository.findByPost_NeighbourhoodId(1)).thenReturn(reports);
        when(postService.getPostById(1)).thenReturn(null);

        List<ReportDTO> result = reportService.getReportedPosts(1);

        assertEquals(1, result.size());
        ReportDTO reportDTO = result.get(0);
        verify(reportRepository).findByPost_NeighbourhoodId(1);
        verify(postService).getPostById(1);
    }

    @Test
    void approvePost_updatesReportStatus() {
        when(reportRepository.findById(1)).thenReturn(Optional.of(testReport));

        reportService.approvePost(1);

        assertEquals(ReportStatus.REVIEWED, testReport.getReportStatus());
        verify(reportRepository).findById(1);
        verify(reportRepository).save(testReport);
    }

    @Test
    void approvePost_reportNotFound_throwsException() {
        when(reportRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reportService.approvePost(1)
        );
        assertEquals("Report not found", exception.getMessage());
        verify(reportRepository).findById(1);
        verify(reportRepository, never()).save(any());
    }

    @Test
    void deletePost_removesPostAndUpdatesStatus() {
        when(reportRepository.findById(1)).thenReturn(Optional.of(testReport));

        reportService.deletePost(1);

        assertEquals(ReportStatus.RESOLVED, testReport.getReportStatus());
        verify(reportRepository).findById(1);
        verify(postRepository).deleteById(1);
    }

    @Test
    void deletePost_reportNotFound_throwsException() {
        when(reportRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reportService.deletePost(1)
        );
        assertEquals("Report not found", exception.getMessage());
        verify(reportRepository).findById(1);
        verify(postRepository, never()).deleteById(anyInt());
        verify(reportRepository, never()).save(any());
    }
}