package com.tsengfhy.elaphure.util;

import com.tsengfhy.elaphure.exception.SensitiveCharacterFoundException;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.owasp.validator.html.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@UtilityClass
public final class XssUtils {

    @Setter
    private static ProcessStrategy defaultProcessStrategy;
    private static final AntiSamy ANTI_SAMY;

    static {
        try (InputStream is = new ClassPathResource("antisamy.xml").getInputStream()) {
            ANTI_SAMY = new AntiSamy(Policy.getInstance(is));
        } catch (PolicyException | IOException e) {
            throw new IllegalStateException("Please ensure classpath:antisamy.xml exists with correct format", e);
        }
    }

    public static String process(@Nullable String value) {
        return process(value, null);
    }

    @SneakyThrows({PolicyException.class, ScanException.class})
    public static String process(@Nullable String value, ProcessStrategy processStrategy) {
        ProcessStrategy strategy = Optional.ofNullable(processStrategy).orElse(defaultProcessStrategy);
        String result = value;
        if (StringUtils.isNotBlank(value)) {
            CleanResults cr = ANTI_SAMY.scan(value);
            if (cr.getNumberOfErrors() > 0) {
                log.warn("Sensitive character found in '{}', and processing strategy is {}", value, strategy);
                switch (strategy) {
                    case REJECT:
                        throw new SensitiveCharacterFoundException();
                    case FILTER:
                        result = cr.getCleanHTML();
                        break;
                    case ENCODE:
                        result = StringEscapeUtils.escapeHtml4(value);
                        break;
                    default:
                }
            }
        }
        return result;
    }

    public enum ProcessStrategy {
        REJECT,
        FILTER,
        ENCODE,
    }
}
