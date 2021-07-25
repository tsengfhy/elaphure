package com.tsengfhy.elaphure.web.cors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class OrderedCorsFilter extends CorsFilter implements OrderedFilter {

    @Getter
    @Setter
    private int order = REQUEST_WRAPPER_FILTER_MAX_ORDER - 100;

    public OrderedCorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }
}
