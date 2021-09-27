package com.tsengfhy.elaphure.env;

import com.tsengfhy.elaphure.constants.Context;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@ConfigurationProperties(prefix = Context.PREFIX + ".web")
public class WebProperties {

    private final Cors cors = new Cors();
    private final Xss xss = new Xss();
    private final OpenApi openApi = new OpenApi();

    @Data
    public static class Cors {
        private boolean enabled = true;
        private List<String> allowedOrigins = Collections.singletonList(CorsConfiguration.ALL);
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
        private ProcessStrategy processStrategy = ProcessStrategy.FILTER;

        public enum ProcessStrategy {
            REJECT,
            FILTER,
            ENCODE,
        }
    }

    @Data
    public static class OpenApi {
        private String title;
        private String description;
        private String version;
        private String termsOfService;
        private String contact;
        private String url;
        private String mail;

        private List<String> basePackages = Collections.emptyList();
        private Map<String, List<String>> groupMap = Collections.emptyMap();
    }
}
