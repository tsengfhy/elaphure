package com.tsengfhy.elaphure.mapper;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
class Domain {
    private LocalDate date;
}
