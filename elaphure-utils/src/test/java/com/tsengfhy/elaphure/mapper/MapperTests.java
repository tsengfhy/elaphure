package com.tsengfhy.elaphure.mapper;

import com.tsengfhy.elaphure.constant.DateFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class MapperTests {

    @Test
    void testTo() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormat.ISO_DATE);

        DomainMapper mapper = DomainMapper.INSTANCE;
        DomainDTO target = mapper.to(new Domain().setDate(now));
        Assertions.assertEquals(formatter.format(now), target.getDate());
        Domain source = mapper.toInverse(target);
        Assertions.assertEquals(now, source.getDate());
    }

    @Test
    void testUpdate() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateFormat.ISO_DATE);

        DomainMapper mapper = DomainMapper.INSTANCE;
        DomainDTO target = new DomainDTO();
        mapper.update(new Domain().setDate(now), target);
        Assertions.assertEquals(formatter.format(now), target.getDate());
        Domain source = new Domain();
        mapper.updateInverse(target, source);
        Assertions.assertEquals(now, source.getDate());
    }
}
