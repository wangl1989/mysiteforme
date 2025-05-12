package com.mysiteforme.admin.entity;

import com.mysiteforme.admin.entity.request.BaseUserRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateCurrentUserRequest extends BaseUserRequest {

    private Long id;

    private String location;
}
