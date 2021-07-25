package com.tsengfhy.elaphure.web.servlet.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Getter
@Setter
public class OrderedCorsFilter extends CorsFilter implements OrderedFilter {

    private int order = REQUEST_WRAPPER_FILTER_MAX_ORDER - 103;

    public OrderedCorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }
}
