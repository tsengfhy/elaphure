package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SmartEntity extends BaseEntity {

    @TableId
    private Long id;

    @Version
    private Integer version;
    @TableLogic
    private String deleted;
}
