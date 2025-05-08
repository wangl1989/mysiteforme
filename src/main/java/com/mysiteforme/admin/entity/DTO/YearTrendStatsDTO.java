package com.mysiteforme.admin.entity.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class YearTrendStatsDTO {

    private LocalDate statDate;

    private Integer monthlyTotalVisits;
}
