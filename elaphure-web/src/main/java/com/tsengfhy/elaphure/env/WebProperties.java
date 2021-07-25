package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import com.tsengfhy.elaphure.utils.XssUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".web")
public class WebProperties {

    private final Cors cors = new Cors();
    private final Xss xss = new Xss();

    @Data
    public static class Cors {
        private boolean enabled = true;
        private List<String> allowedOriginPatterns = Collections.singletonList(CorsConfiguration.ALL);
        private List<String> allowedMethods = Stream.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE).map(HttpMethod::name).collect(Collectors.toList());
        private List<String> allowedHeaders = Collections.singletonList(CorsConfiguration.ALL);
        private List<String> exposedHeaders = Collections.singletonList(HttpHeaders.LOCATION);
        private boolean allowCredentials = true;
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration maxAge = Duration.ofSeconds(1800);
    }

    @Data
    public static class Xss {
        private boolean enabled = true;
        private List<String> allowedPaths = Collections.emptyList();
        private XssUtils.ProcessStrategy processStrategy = XssUtils.ProcessStrategy.FILTER;
    }
}
