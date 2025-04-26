package com.dalhousie.Neighbourly.report.repository;


import com.dalhousie.Neighbourly.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Query("SELECT p FROM Report p WHERE p.neighbourhoodid = :neighbourhoodId AND p.reportStatus = 'PENDING' ")
    List<Report> findByPost_NeighbourhoodId(int neighbourhoodId);



}
