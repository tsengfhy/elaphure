package com.tsengfhy.elaphure.config;

import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.MailerGenericBuilder;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Message;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Message.class})
@Conditional(SesConfig.SesCondition.class)
@AutoConfigureBefore(MailSenderAutoConfiguration.class)
@EnableConfigurationProperties(MailProperties.class)
public class SesConfig extends SimpleJavaMailSpringSupport {

    public SesConfig() {
        ConfigLoader.loadProperties("mail.properties", true);
    }

    @Autowired
    private MailProperties mailProperties;

    /**
     * Parameters priority list as below:
     * 1.Start with 'spring.mail.'
     * 2.Start with 'simplejavamail.'
     * 3.In mail.properties
     */
    @Bean
    @Override
    public Mailer defaultMailer(MailerGenericBuilder<?> defaultMailerBuilder) {
        applySpringProperties();
        applyDefaultFrom();
        return MailerBuilder.buildMailer();
    }

    private void applySpringProperties() {
        final Properties properties = new Properties();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Optional.ofNullable(mailProperties.getProtocol()).filter(value -> StringUtils.startsWithIgnoreCase(value, "SMTP")).orElseThrow(() -> new IllegalStateException("Mail protocol only support SMTP"));
        mapper.from(mailProperties.getProtocol()).to(value -> properties.setProperty(ConfigLoader.Property.TRANSPORT_STRATEGY.key(), StringUtils.upperCase(value)));
        mapper.from(mailProperties.getHost()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_HOST.key(), value));
        mapper.from(mailProperties.getPort()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_PORT.key(), String.valueOf(value)));
        mapper.from(mailProperties.getUsername()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_USERNAME.key(), value));
        mapper.from(mailProperties.getPassword()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_PASSWORD.key(), value));
        Optional.of(mailProperties.getProperties()).filter(Map::isEmpty).orElseThrow(() -> new IllegalStateException("Please use properties start with 'simplejavamail.' instead of 'spring.mail.properties'"));
        ConfigLoader.loadProperties(properties, true);
    }

    private void applyDefaultFrom() {
        if (ConfigLoader.hasProperty(ConfigLoader.Property.SMTP_USERNAME)) {
            final Properties properties = new Properties();
            properties.setProperty(ConfigLoader.Property.DEFAULT_FROM_ADDRESS.key(), ConfigLoader.getProperty(ConfigLoader.Property.SMTP_USERNAME));
            ConfigLoader.loadProperties(properties, true);
        }
    }

    @Bean
    public JavaMailSenderImpl mailSender(Mailer mailer) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setSession(mailer.getSession());
        applyProperties(sender);
        return sender;
    }

    private void applyProperties(JavaMailSenderImpl sender) {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(ConfigLoader.getStringProperty(ConfigLoader.Property.SMTP_HOST)).to(sender::setHost);
        mapper.from(ConfigLoader.getStringProperty(ConfigLoader.Property.SMTP_PORT)).to(value -> sender.setPort(Integer.parseInt(value)));
        mapper.from(ConfigLoader.getStringProperty(ConfigLoader.Property.SMTP_USERNAME)).to(sender::setUsername);
        mapper.from(ConfigLoader.getStringProperty(ConfigLoader.Property.SMTP_PASSWORD)).to(sender::setPassword);
        mapper.from(mailProperties.getDefaultEncoding()).to(value -> sender.setDefaultEncoding(value.name()));
    }

    static class SesCondition extends AnyNestedCondition {

        SesCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = "host")
        static class SpringMailProperty {
        }

        @ConditionalOnProperty(prefix = "simplejavamail.smtp", name = "host")
        static class SimpleJavaMailProperty {
        }
    }
}
