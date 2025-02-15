package com.mysiteforme.admin.redis;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode
public class CacheEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Rdis的value
     */
    private Object value;
    /**
     * 保存的时间戳
     */
    private long gmtModify;
    /**
     * 过期时间
     */
    private int expire;
 
}
