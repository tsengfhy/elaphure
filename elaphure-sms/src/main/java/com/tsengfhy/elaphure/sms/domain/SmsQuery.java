package com.tsengfhy.elaphure.sms.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SmsQuery {
    private String id;
    private String phone;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Integer pageNo;
    private Integer pageSize;
}
