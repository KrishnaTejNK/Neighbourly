package com.dalhousie.Neighbourly.report.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportid;

    @Column(nullable = true)
    private int postid;  // The reported post

    @Column(nullable = true)
    private int userid; // Who reported the post

    @Column(nullable = true)
    private Integer neighbourhoodid;

    private LocalDateTime reportedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.PENDING;



    public Report(int userid, Integer neighbourhoodid, int postid, ReportStatus reportStatus) {
        this.userid = userid;
        this.neighbourhoodid = neighbourhoodid;
        this.postid = postid;
        this.reportStatus = reportStatus;
    }


}
