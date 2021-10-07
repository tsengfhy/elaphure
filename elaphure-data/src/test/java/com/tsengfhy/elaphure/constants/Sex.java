package com.tsengfhy.elaphure.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Sex {
    MALE("male", "男"),
    FEMALE("female", "女");

    @EnumValue
    private final String key;
    private final String label;
}
