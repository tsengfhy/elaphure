package com.tsengfhy.elaphure.domain;

import com.tsengfhy.elaphure.constants.Sex;
import com.tsengfhy.elaphure.data.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class User extends BaseEntity {
    private String name;
    private Sex sex;
    private Integer age;
    private String email;
}
