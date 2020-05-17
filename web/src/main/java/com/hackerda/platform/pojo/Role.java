package com.hackerda.platform.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Role {
    private Integer id;

    private String name;

    private String code;
}