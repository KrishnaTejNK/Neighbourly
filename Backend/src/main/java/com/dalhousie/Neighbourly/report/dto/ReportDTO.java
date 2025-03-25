package com.dalhousie.Neighbourly.report.dto;


import com.dalhousie.Neighbourly.post.dto.PostResponseDTO;
import com.dalhousie.Neighbourly.report.entity.Report;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private int id;
    private int postId;
    private List<PostResponseDTO> posts;
    private int userId;
    private int neighbourhoodId;
    private LocalDateTime reportedAt;
    private Report.ReportStatus reportStatus;
}
