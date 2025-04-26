package com.dalhousie.Neighbourly.report.service;


import com.dalhousie.Neighbourly.report.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    void reportPost(int neighbouhoodid,int postid,int userId);
    List<ReportDTO> getReportedPosts(int neighbourhoodId);
    void approvePost(int reportId);
    void deletePost(int reportId);
}
