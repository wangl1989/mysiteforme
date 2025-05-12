package com.mysiteforme.admin.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisDataResponse implements Serializable {

    /**
     * Redis key
     */
    private String key;

    /**
     * Redis data type (string, list, hash, set, zset)
     */
    private String type;

    /**
     * Time to live in seconds, -1 if no expiration, -2 if key does not exist
     */
    private Long ttl;

    /**
     * Redis value as string representation
     */
    private String value;

}
