package com.tsengfhy.elaphure.sms.mapper;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.tsengfhy.elaphure.constants.DateFormat;
import com.tsengfhy.elaphure.sms.domain.SmsQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SmsQueryMapper {

    SmsQueryMapper INSTANCE = Mappers.getMapper(SmsQueryMapper.class);

    @Mapping(source = "id", target = "bizId")
    @Mapping(source = "phone", target = "phoneNumber")
    @Mapping(source = "fromDate", target = "sendDate", dateFormat = DateFormat.PURE_DATE_FORMAT)
    @Mapping(source = "pageNo", target = "currentPage")
    QuerySendDetailsRequest to(SmsQuery query);
}
