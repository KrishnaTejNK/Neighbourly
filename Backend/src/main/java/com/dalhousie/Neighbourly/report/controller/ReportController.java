package com.dalhousie.Neighbourly.report.controller;


import com.dalhousie.Neighbourly.report.dto.ReportDTO;
import com.dalhousie.Neighbourly.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    private final ReportService reportService;


    @PostMapping("/report")
    public ResponseEntity<String> reportPost(@RequestBody Map<String, Object> request) {
        int neighbourhoodId = Integer.parseInt(request.get("neighbourhoodId").toString());
        int postId = Integer.parseInt( request.get("postId").toString());
        int userId = Integer.parseInt(request.get("reporterId").toString());

        reportService.reportPost(neighbourhoodId, postId, userId);
        return ResponseEntity.ok("Post reported successfully.");
    }


    @GetMapping("/{neighbourhoodId}")
    public ResponseEntity<List<ReportDTO>> getReportedPosts(@PathVariable int neighbourhoodId) {
        return ResponseEntity.ok(reportService.getReportedPosts(neighbourhoodId));
    }

    @PutMapping("/approve/{reportId}")
    public ResponseEntity<String> approvePost(@PathVariable int reportId) {
        reportService.approvePost(reportId);
        return ResponseEntity.ok("Post approved.");
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deletePost(@PathVariable int reportId) {
        reportService.deletePost(reportId);
        return ResponseEntity.ok("Post deleted.");
    }
}
