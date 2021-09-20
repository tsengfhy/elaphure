package com.tsengfhy.elaphure.gateway;

import lombok.Builder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.util.Assert;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider, InitializingBean {

    private final RouteLocator routeLocator;
    private final String routeIdPrefix;
    private final String applicationName; // used to exclude itself
    private final String openApiPath;
    private final Pattern pathPredicatePattern = Pattern.compile("^Paths: \\[(\\S+)\\]");

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(routeLocator, "RouteLocator must not be null");
        Assert.notNull(routeIdPrefix, "RouteIdPrefix must not be null");
        Assert.notNull(applicationName, "ApplicationName must not be null");
        Assert.notNull(openApiPath, "OpenApiPath must not be null");
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();

        routeLocator.getRoutes().subscribe(route -> {
            String contextName = route.getId().replace(routeIdPrefix, "");
            Matcher matcher = pathPredicatePattern.matcher(route.getPredicate().toString());
            if (!applicationName.startsWith(contextName) && matcher.find()) {
                // contextPath should be handled in the routing rules
                resources.add(swaggerResource(contextName, matcher.group(1).replace("/**", openApiPath)));
            }
        });

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(DocumentationType.OAS_30.getVersion());
        return swaggerResource;
    }
}
