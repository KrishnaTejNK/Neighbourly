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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public void reportPost(int neighbouhoodid,int postId, int userId) {
        Report report = new Report();
        report.setUserid(userId);
        report.setNeighbourhoodid(neighbouhoodid);
        report.setPostid(postId);
        report.setReportStatus(Report.ReportStatus.PENDING);
        reportRepository.save(report);
    }

    @Override
    public List<ReportDTO> getReportedPosts(int neighbourhoodId) {
        // Fetch all reports for the given neighborhood
        List<Report> reports = reportRepository.findByPost_NeighbourhoodId(neighbourhoodId);

        // Fetch post details only for reported posts
        List<PostResponseDTO> reportedPosts = reports.stream()
                .map(report -> postService.getPostById(report.getPostid())) // Fetch post details for each reported post
                .toList();

        // Convert to DTO format
        return reports.stream().map(report -> new ReportDTO(
                report.getReportid(),
                report.getPostid(),
                reportedPosts.stream()
                        .filter(post -> post.getPostId() == report.getPostid()) // Filter to get the correct post details
                        .findFirst()
                        .map(List::of) // Convert to a single-item list to match the DTO structure
                        .orElse(List.of()), // If no post found, return an empty list
                report.getUserid(),
                report.getNeighbourhoodid(),
                report.getReportedAt(),
                report.getReportStatus()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approvePost(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setReportStatus(Report.ReportStatus.REVIEWED);
        reportRepository.save(report);
    }

    @Override
    @Transactional
    public void deletePost(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setReportStatus(Report.ReportStatus.RESOLVED);
        postRepository.deleteById(report.getPostid());

    }
}
