package com.tsengfhy.elaphure.data;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (this.openInsertFill()) {
            this.fillStrategy(metaObject, "createdDate", LocalDateTime.now());
            this.fillStrategy(metaObject, "lastModifiedDate", LocalDateTime.now());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (this.openUpdateFill()) {
            this.fillStrategy(metaObject, "lastModifiedDate", LocalDateTime.now());
        }
    }
}
