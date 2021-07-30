package com.tsengfhy.elaphure.utils;

import com.tsengfhy.elaphure.env.WebProperties;
import com.tsengfhy.elaphure.web.xss.SensitiveCharacterFoundException;
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
    private static WebProperties.Xss xss;
    private static final AntiSamy ANTI_SAMY;

    static {
        try (InputStream is = new ClassPathResource("antisamy.xml").getInputStream()) {
            ANTI_SAMY = new AntiSamy(Policy.getInstance(is));
        } catch (PolicyException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SneakyThrows({PolicyException.class, ScanException.class})
    public static String process(@Nullable String value) {
        Optional.ofNullable(xss).orElseThrow(UnsupportedOperationException::new);
        String result = value;
        if (StringUtils.isNotBlank(value)) {
            CleanResults cr = ANTI_SAMY.scan(StringEscapeUtils.unescapeHtml4(value));
            if (cr.getNumberOfErrors() > 0) {
                log.warn("Sensitive character found in '{}', and processing strategy is {}", value, xss.getProcessStrategy());
                switch (xss.getProcessStrategy()) {
                    case REJECT:
                        throw new SensitiveCharacterFoundException();
                    case FILTER:
                        result = cr.getCleanHTML();
                        break;
                    case ENCODE:
                        result = StringEscapeUtils.escapeHtml4(value);
                        break;
                    default:
                        //Noting to do
                }
            }
        }
        return result;
    }
}
