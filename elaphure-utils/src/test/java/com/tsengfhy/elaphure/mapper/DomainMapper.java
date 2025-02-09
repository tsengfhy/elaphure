package com.tsengfhy.elaphure.mapper;

import com.tsengfhy.elaphure.constant.DateFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface DomainMapper extends BaseMapper<Domain, DomainDTO> {

    DomainMapper INSTANCE = Mappers.getMapper(DomainMapper.class);

    @Mapping(target = "date", dateFormat = DateFormat.ISO_DATE)
    DomainDTO to(Domain domain);
}
