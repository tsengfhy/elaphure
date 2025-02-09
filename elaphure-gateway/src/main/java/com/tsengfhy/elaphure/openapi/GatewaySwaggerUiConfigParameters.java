package com.tsengfhy.elaphure.openapi;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class GatewaySwaggerUiConfigParameters extends SwaggerUiConfigParameters {

    private final SpringDocConfigProperties springDocConfigProperties;
    private final RouteLocator routeLocator;

    public GatewaySwaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfig, SpringDocConfigProperties springDocConfigProperties, RouteLocator routeLocator) {
        super(swaggerUiConfig);
        Assert.notNull(swaggerUiConfig, "SwaggerUiConfigProperties must not be null");
        Assert.notNull(springDocConfigProperties, "SpringDocConfigProperties must not be null");
        Assert.notNull(routeLocator, "RouteLocator must not be null");
        Assert.isTrue(!springDocConfigProperties.isUseManagementPort(), "Use management port alone with Gateway is not supported");
        this.springDocConfigProperties = springDocConfigProperties;
        this.routeLocator = routeLocator;
    }

    @Override
    public Map<String, Object> getConfigParameters() {
        Map<String, Object> params = super.getConfigParameters();
        final Set<SwaggerUrl> swaggerUrls = Optional.ofNullable((Set<SwaggerUrl>) params.get(URLS_PROPERTY)).orElseGet(HashSet::new);
        routeLocator.getRoutes().subscribe(route -> {
            if ("lb".equals(route.getUri().getScheme())) {
                String name = route.getUri().getHost();
                SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, "/" + name + springDocConfigProperties.getApiDocs().getPath(), name);
                swaggerUrls.add(swaggerUrl);
            }
        });

        Comparator<SwaggerUrl> swaggerUrlComparator;
        if ("ASC".equals(String.valueOf(groupsOrder))) {
            swaggerUrlComparator = Comparator.comparing(SwaggerUrl::getDisplayName);
        } else {
            swaggerUrlComparator = (h1, h2) -> h2.getDisplayName().compareTo(h1.getDisplayName());
        }

        params.put(URLS_PROPERTY, swaggerUrls.stream().sorted(swaggerUrlComparator).filter(swaggerUrl -> StringUtils.isNotEmpty(swaggerUrl.getUrl())).collect(Collectors.toCollection(LinkedHashSet::new)));
        return params;
    }
}
