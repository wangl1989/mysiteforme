package com.mysiteforme.admin.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by wangl on 2017/11/27.
 * todo:
 */
@Getter
@Setter
@Data
public class LayerData<T> {
    private Integer code = 0;
    private Integer count;
    private List<T> data;
    private String msg = "";

}
