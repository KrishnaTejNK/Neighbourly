package com.dalhousie.Neighbourly.report.controller;

import com.dalhousie.Neighbourly.report.dto.ReportDTO;
import com.dalhousie.Neighbourly.report.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ReportController.
 */
@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private static final int TEST_REPORT_ID = 1;
    private static final int TEST_POST_ID = 2;
    private static final int TEST_NEIGHBOURHOOD_ID = 3;
    private static final int TEST_REPORTER_ID = 4;
    private static final int EXPECTED_CALL_COUNT = 1;

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private ObjectMapper objectMapper;

    private ReportDTO testReportDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();

        testReportDTO = new ReportDTO();
        testReportDTO.setUserId(TEST_REPORT_ID); // Assuming this was meant as report ID or similar
        testReportDTO.setPostId(TEST_POST_ID);
        testReportDTO.setNeighbourhoodId(TEST_NEIGHBOURHOOD_ID);
        testReportDTO.setUserId(TEST_REPORTER_ID); // Assuming this maps to reporterId based on test usage
    }

    @Test
    void reportPost_successful_returnsOk() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("neighbourhoodId", TEST_NEIGHBOURHOOD_ID);
        request.put("postId", TEST_POST_ID);
        request.put("reporterId", TEST_REPORTER_ID);
        doNothing().when(reportService).reportPost(TEST_NEIGHBOURHOOD_ID, TEST_POST_ID, TEST_REPORTER_ID);

        // Act & Assert
        mockMvc.perform(post("/api/reports/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post reported successfully."));

        verify(reportService, times(EXPECTED_CALL_COUNT)).reportPost(TEST_NEIGHBOURHOOD_ID, TEST_POST_ID, TEST_REPORTER_ID);
    }

    @Test
    void approvePost_successful_returnsOk() throws Exception {
        // Arrange
        doNothing().when(reportService).approvePost(TEST_REPORT_ID);

        // Act & Assert
        mockMvc.perform(put("/api/reports/approve/" + TEST_REPORT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Post approved."));

        verify(reportService, times(EXPECTED_CALL_COUNT)).approvePost(TEST_REPORT_ID);
    }

    @Test
    void deletePost_successful_returnsOk() throws Exception {
        // Arrange
        doNothing().when(reportService).deletePost(TEST_REPORT_ID);

        // Act & Assert
        mockMvc.perform(delete("/api/reports/delete/" + TEST_REPORT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Post deleted."));

        verify(reportService, times(EXPECTED_CALL_COUNT)).deletePost(TEST_REPORT_ID);
    }
}