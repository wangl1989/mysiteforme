package com.mysiteforme.admin.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class YearTrendStatsDataResponse implements Serializable {

    // 日期标签
    private List<String> months;

    private List<Integer> visits;

}
