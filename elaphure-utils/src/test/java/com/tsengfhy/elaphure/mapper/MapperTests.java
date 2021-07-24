package com.tsengfhy.elaphure.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MapperTests {

    @Test
    void testMapper() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        DomainMapper mapper = DomainMapper.INSTANCE;
        DomainMapper.DomainDTO dto = mapper.to(new DomainMapper.Domain().setDate(now));
        Assertions.assertEquals(formatter.format(now), dto.getDateStr());
        DomainMapper.Domain domain = mapper.from(dto);
        Assertions.assertEquals(now, domain.getDate());
    }
}
