package com.tsengfhy.entry.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class Domain {

    @NotEmpty
    private String value;
}
