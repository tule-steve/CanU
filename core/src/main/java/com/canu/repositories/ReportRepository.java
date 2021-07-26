package com.canu.repositories;

import com.canu.model.ReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReportRepository extends JpaRepository<ReportModel, Long>, JpaSpecificationExecutor<ReportModel> {

    @Modifying
    @Query(value = "insert into report(revenue, report_date)\n" +
                   "select SUM(p.total/prop.value), DATE_FORMAT(min(j.completed_at),'%y-%m-%d') from job j\n" +
                   "inner join payment p ON p.job_id = j.id\n" +
                   "inner join property prop on prop.type = 'POINT_EXCHANGE' and prop.field = p.currency\n" +
                   "where j.status = 'COMPLETED'" +
                   "and j.completed_at between ?1 and ?2 \n" +
                   "group by Year(j.completed_at), month(j.completed_at), day(j.completed_at)\n", nativeQuery = true)
    void updateAvenue(LocalDateTime startDate, LocalDateTime endDate);
}
