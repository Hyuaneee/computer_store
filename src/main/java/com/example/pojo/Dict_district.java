package com.example.pojo;

import lombok.Data;

//省，市，区信息
@Data
public class Dict_district {
    private Long id;
    private String parent;
    private String code;
    private String name;
}

