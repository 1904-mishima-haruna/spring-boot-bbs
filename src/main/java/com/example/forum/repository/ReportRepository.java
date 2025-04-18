package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    // 匿名掲示板サンプルの中で実装した ReportRepository は JpaRepository を継承

    public List<Report> findAllByOrderByIdDesc();

    public List<Report> findByUpdatedDateBetween(Date startDate, Date endDate);
    public List<Report> findByUpdatedDateBetweenOrderByUpdatedDateDesc(Date startDate, Date endDate);

}
