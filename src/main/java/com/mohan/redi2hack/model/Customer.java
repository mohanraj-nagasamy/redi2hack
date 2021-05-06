package com.mohan.redi2hack.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@RedisHash
public class Customer implements Serializable {
    @Id
    private Long id;

    private String name;

    private String industry;

    public static Customer from(Map<String, String> map) {
        return Customer.builder()
                .id(Long.valueOf(map.get("id")))
                .name(map.get("name"))
                .industry(map.get("industry"))
                .build();
    }
}
