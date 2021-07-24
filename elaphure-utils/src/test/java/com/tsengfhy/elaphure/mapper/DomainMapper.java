package com.tsengfhy.elaphure.mapper;

import com.tsengfhy.elaphure.constants.DateFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper
public interface DomainMapper extends BaseMapper<DomainMapper.Domain, DomainMapper.DomainDTO> {

    DomainMapper INSTANCE = Mappers.getMapper(DomainMapper.class);

    @Mapping(source = "date", target = "dateStr", dateFormat = DateFormat.DATE_FORMAT)
    DomainMapper.DomainDTO to(DomainMapper.Domain domain);

    @Data
    @Accessors(chain = true)
    class Domain {
        private LocalDate date;
    }

    @Data
    class DomainDTO {
        private String dateStr;
    }
}
