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
/**
 * Layer数据封装类
 * 用于前端Layer组件的数据格式封装
 * @param <T> 数据类型
 */
public class LayerData<T> {
    /** 状态码，默认为0 */
    private Integer code = 0;
    
    /** 数据总数 */
    private Integer count;
    
    /** 数据列表 */
    private List<T> data;
    
    /** 提示信息，默认为空字符串 */
    private String msg = "";

}
