package com.tsengfhy.entry.domain;

import com.tsengfhy.elaphure.data.SmartEntity;
import com.tsengfhy.entry.constants.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class User extends SmartEntity {
    private String name;
    private Sex sex;
    private Integer age;
    private String email;
}
