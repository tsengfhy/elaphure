package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {
    @TableId
    private Long id;

    @Version
    private Integer version;
    @TableLogic
    private String deleted;
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long lastModifiedBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastModifiedDate;
}
