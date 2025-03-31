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

    private static final int TEST_REPORT_ID = 1;
    private static final int TEST_POST_ID = 1;
    private static final int TEST_USER_ID = 1;
    private static final int TEST_NEIGHBOURHOOD_ID = 1;
    private static final int EXPECTED_LIST_SIZE = 1;
    private static final int EXPECTED_CALL_COUNT = 1;

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
        testReport = new Report(TEST_REPORT_ID, TEST_POST_ID, TEST_USER_ID, ReportStatus.PENDING);
        testReport.setReportid(TEST_REPORT_ID);
        testReport.setReportedAt(LocalDateTime.now());

        testPostResponse = new PostResponseDTO();
        testPostResponse.setPostId(TEST_POST_ID);
        testPostResponse.setUserId(TEST_USER_ID);
    }

    @Test
    void reportPost_savesReportSuccessfully() {
        reportService.reportPost(TEST_NEIGHBOURHOOD_ID, TEST_POST_ID, TEST_USER_ID);

        verify(reportRepository, times(EXPECTED_CALL_COUNT)).save(argThat(report ->
                report.getNeighbourhoodid() == TEST_NEIGHBOURHOOD_ID &&
                        report.getPostid() == TEST_POST_ID &&
                        report.getUserid() == TEST_USER_ID &&
                        report.getReportStatus() == ReportStatus.PENDING
        ));
    }

    @Test
    void getReportedPosts_returnsReportDTOList() {
        List<Report> reports = List.of(testReport);
        when(reportRepository.findByPost_NeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(reports);
        when(postService.getPostById(TEST_POST_ID)).thenReturn(testPostResponse);

        List<ReportDTO> result = reportService.getReportedPosts(TEST_NEIGHBOURHOOD_ID);

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        ReportDTO reportDTO = result.get(0);
        assertEquals(TEST_REPORT_ID, reportDTO.getId());
        assertEquals(TEST_POST_ID, reportDTO.getPostId());
        assertEquals(TEST_USER_ID, reportDTO.getUserId());
        assertEquals(TEST_NEIGHBOURHOOD_ID, reportDTO.getNeighbourhoodId());
        assertEquals(ReportStatus.PENDING, reportDTO.getReportStatus());
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findByPost_NeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(postService, times(EXPECTED_CALL_COUNT)).getPostById(TEST_POST_ID);
    }

    @Test
    void getReportedPosts_postNotFound_returnsEmptyPostDetails() {
        List<Report> reports = List.of(testReport);
        when(reportRepository.findByPost_NeighbourhoodId(TEST_NEIGHBOURHOOD_ID)).thenReturn(reports);
        when(postService.getPostById(TEST_POST_ID)).thenReturn(null);

        List<ReportDTO> result = reportService.getReportedPosts(TEST_NEIGHBOURHOOD_ID);

        assertEquals(EXPECTED_LIST_SIZE, result.size());
        ReportDTO reportDTO = result.get(0);
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findByPost_NeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        verify(postService, times(EXPECTED_CALL_COUNT)).getPostById(TEST_POST_ID);
    }

    @Test
    void approvePost_updatesReportStatus() {
        when(reportRepository.findById(TEST_REPORT_ID)).thenReturn(Optional.of(testReport));

        reportService.approvePost(TEST_REPORT_ID);

        assertEquals(ReportStatus.REVIEWED, testReport.getReportStatus());
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REPORT_ID);
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).save(testReport);
    }

    @Test
    void approvePost_reportNotFound_throwsException() {
        when(reportRepository.findById(TEST_REPORT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reportService.approvePost(TEST_REPORT_ID)
        );
        assertEquals("Report not found", exception.getMessage());
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REPORT_ID);
        verify(reportRepository, never()).save(any());
    }

    @Test
    void deletePost_removesPostAndUpdatesStatus() {
        when(reportRepository.findById(TEST_REPORT_ID)).thenReturn(Optional.of(testReport));

        reportService.deletePost(TEST_REPORT_ID);

        assertEquals(ReportStatus.RESOLVED, testReport.getReportStatus());
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REPORT_ID);
        verify(postRepository, times(EXPECTED_CALL_COUNT)).deleteById(TEST_POST_ID);
    }

    @Test
    void deletePost_reportNotFound_throwsException() {
        when(reportRepository.findById(TEST_REPORT_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reportService.deletePost(TEST_REPORT_ID)
        );
        assertEquals("Report not found", exception.getMessage());
        verify(reportRepository, times(EXPECTED_CALL_COUNT)).findById(TEST_REPORT_ID);
        verify(postRepository, never()).deleteById(anyInt());
        verify(reportRepository, never()).save(any());
    }
}