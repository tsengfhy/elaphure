package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdDate", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedDate", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedDate", LocalDateTime.class, LocalDateTime.now());
    }
}
