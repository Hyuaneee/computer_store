package com.example.pojo;

import lombok.Data;

/**
 * 地址类（省，市，区）
 */
@Data
public class Dict_district {
    private Long id;
    private String parent;
    private String code;
    private String name;
}

