package com.mysiteforme.admin.entity.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BasePageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer page = 1;

    private Integer limit = 10;

}
