package com.tsengfhy.elaphure.sms.domain;

import com.tsengfhy.elaphure.sms.constants.SmsStatus;
import com.tsengfhy.elaphure.sms.constants.SmsType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class SmsMessage implements Serializable {
    private static final long serialVersionUID = -507708694094129832L;

    private String id;
    private String extId;
    private String phone;
    private String sign;
    private String template;
    private Map<String, String> params = new HashMap<>();
    private String content;
    private SmsType type = SmsType.DOWN;
    private SmsStatus status;
    private String message;
    private LocalDateTime sendDate;

    public SmsMessage addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }
}
