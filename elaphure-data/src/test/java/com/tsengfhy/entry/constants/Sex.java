package com.tsengfhy.entry.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sex {
    MALE("male", "男"),
    FEMALE("female", "女");

    @EnumValue
    private final String key;
    private final String label;
}
