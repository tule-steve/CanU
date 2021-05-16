package com.canu.model;

import lombok.Data;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "report")
public class ReportModel {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "cani_id", columnDefinition = "decimal(15, 2)", precision = 15, scale = 2)
    BigDecimal revenue;

    @Column(name = "job_id")
    LocalDate job_id;
}
