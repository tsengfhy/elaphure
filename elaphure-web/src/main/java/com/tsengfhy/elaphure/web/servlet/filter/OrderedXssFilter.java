package com.tsengfhy.elaphure.web.servlet.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

@Getter
@Setter
public class OrderedXssFilter extends XssFilter implements OrderedFilter {

    private int order = REQUEST_WRAPPER_FILTER_MAX_ORDER - 101;
}
