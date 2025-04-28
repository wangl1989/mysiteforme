package com.mysiteforme.admin.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class DownloadCodeRequest {

    private List<Long> ids;
}
