package com.tsengfhy.elaphure.mapper;

import com.tsengfhy.elaphure.utils.JsonUtils;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Named;

public interface BaseMapper<S, T> {

    T to(S s);

    @InheritInverseConfiguration(name = "to")
    S from(T t);

    @Named("toString")
    default <E> String toString(E e) {
        return JsonUtils.toJson(e);
    }
}