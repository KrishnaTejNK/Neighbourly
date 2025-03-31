package com.dalhousie.Neighbourly.report.service;


import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.post.repository.PostRepository;
import com.dalhousie.Neighbourly.post.service.PostService;
import com.dalhousie.Neighbourly.report.dto.ReportDTO;
import com.dalhousie.Neighbourly.report.entity.Report;
import com.dalhousie.Neighbourly.report.entity.ReportStatus;
import com.dalhousie.Neighbourly.report.repository.ReportRepository;
import com.dalhousie.Neighbourly.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    public ReportServiceImpl(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository, PostService postService) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
    }

    @Override
    @Transactional
    public void reportPost(int neighbouhoodid, int postId, int userId) {
        Report report = new Report(userId, neighbouhoodid, postId, ReportStatus.PENDING);
        reportRepository.save(report);
    }

    @Override
    public List<ReportDTO> getReportedPosts(int neighbourhoodId) {
        // Fetch all reports for the given neighborhood
        List<Report> reports = reportRepository.findByPost_NeighbourhoodId(neighbourhoodId);

        // Fetch post details only for reported posts and map to DTO
        return reports.stream()
                .map(report -> {
                    PostResponseDTO postDetails = postService.getPostById(report.getPostid());
                    return new ReportDTO(
                            report.getReportid(),
                            report.getPostid(),
                            postDetails == null ? List.of() : List.of(postDetails), // If post is not found, return empty list
                            report.getUserid(),
                            report.getNeighbourhoodid(),
                            report.getReportedAt(),
                            report.getReportStatus()
                    );
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approvePost(int reportId) {
        Report report = getReportById(reportId);
        report.setReportStatus(ReportStatus.REVIEWED);
        reportRepository.save(report);
    }

    @Override
    @Transactional
    public void deletePost(int reportId) {
        Report report = getReportById(reportId);
        report.setReportStatus(ReportStatus.RESOLVED);
        postRepository.deleteById(report.getPostid());
    }

    // Helper method to get Report by ID and handle exception in one place
    private Report getReportById(int reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }
}
