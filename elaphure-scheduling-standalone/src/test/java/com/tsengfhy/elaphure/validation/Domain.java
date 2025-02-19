package com.tsengfhy.elaphure.validation;

import com.tsengfhy.elaphure.validation.annotation.Cron;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
class Domain {

    @Cron
    private String cron;
}
