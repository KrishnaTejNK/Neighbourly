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
        testReportDTO.setUserId(1); // Assuming setter names match your comment
        testReportDTO.setPostId(2);
        testReportDTO.setNeighbourhoodId(3);
        testReportDTO.setUserId(4);   // Assuming this maps to reporterId
    }

    @Test
    void reportPost_successful_returnsOk() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("neighbourhoodId", 3);
        request.put("postId", 2);
        request.put("reporterId", 4);
        doNothing().when(reportService).reportPost(3, 2, 4);

        // Act & Assert
        mockMvc.perform(post("/api/reports/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Post reported successfully."));

        verify(reportService, times(1)).reportPost(3, 2, 4);
    }


    @Test
    void approvePost_successful_returnsOk() throws Exception {
        // Arrange
        doNothing().when(reportService).approvePost(1);

        // Act & Assert
        mockMvc.perform(put("/api/reports/approve/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Post approved."));

        verify(reportService, times(1)).approvePost(1);
    }

    @Test
    void deletePost_successful_returnsOk() throws Exception {
        // Arrange
        doNothing().when(reportService).deletePost(1);

        // Act & Assert
        mockMvc.perform(delete("/api/reports/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Post deleted."));

        verify(reportService, times(1)).deletePost(1);
    }
}