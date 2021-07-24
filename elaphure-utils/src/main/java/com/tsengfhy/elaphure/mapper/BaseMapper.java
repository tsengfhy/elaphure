package com.tsengfhy.elaphure.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MappingTarget;

public interface BaseMapper<S, T> {

    T to(S s);

    @InheritInverseConfiguration(name = "to")
    S toInverse(T t);

    @InheritConfiguration(name = "to")
    void update(S s, @MappingTarget T t);

    @InheritInverseConfiguration(name = "to")
    void updateInverse(T t, @MappingTarget S s);
}