package com.tsengfhy.elaphure.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.MailerGenericBuilder;
import org.simplejavamail.config.ConfigLoader;
import org.simplejavamail.springsupport.SimpleJavaMailSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Properties;

@AutoConfiguration(after = MailSenderAutoConfiguration.class)
@ConditionalOnClass(Mailer.class)
@Conditional(SESAutoConfiguration.SESCondition.class)
public class SESAutoConfiguration extends SimpleJavaMailSpringSupport {

    @Autowired
    private MailProperties mailProperties;

    @Override
    public MailerGenericBuilder<?> loadGlobalConfigAndCreateDefaultMailer(
            @Value("${simplejavamail.javaxmail.debug:#{null}}") @Nullable String javaxmailDebug,
            @Value("${simplejavamail.transportstrategy:#{null}}") @Nullable String transportstrategy,
            @Value("${simplejavamail.smtp.host:#{null}}") @Nullable String smtpHost,
            @Value("${simplejavamail.smtp.port:#{null}}") @Nullable String smtpPort,
            @Value("${simplejavamail.smtp.username:#{null}}") @Nullable String smtpUsername,
            @Value("${simplejavamail.smtp.password:#{null}}") @Nullable String smtpPassword,
            @Value("${simplejavamail.custom.sslfactory.class:#{null}}") @Nullable String customSSLFactoryClass,
            @Value("${simplejavamail.proxy.host:#{null}}") @Nullable String proxyHost,
            @Value("${simplejavamail.proxy.port:#{null}}") @Nullable String proxyPort,
            @Value("${simplejavamail.proxy.username:#{null}}") @Nullable String proxyUsername,
            @Value("${simplejavamail.proxy.password:#{null}}") @Nullable String proxyPassword,
            @Value("${simplejavamail.proxy.socks5bridge.port:#{null}}") @Nullable String proxySocks5bridgePort,
            @Value("${simplejavamail.defaults.subject:#{null}}") @Nullable String defaultSubject,
            @Value("${simplejavamail.defaults.from.name:#{null}}") @Nullable String defaultFromName,
            @Value("${simplejavamail.defaults.from.address:#{null}}") @Nullable String defaultFromAddress,
            @Value("${simplejavamail.defaults.replyto.name:#{null}}") @Nullable String defaultReplytoName,
            @Value("${simplejavamail.defaults.replyto.address:#{null}}") @Nullable String defaultReplytoAddress,
            @Value("${simplejavamail.defaults.bounceto.name:#{null}}") @Nullable String defaultBouncetoName,
            @Value("${simplejavamail.defaults.bounceto.address:#{null}}") @Nullable String defaultBouncetoAddress,
            @Value("${simplejavamail.defaults.to.name:#{null}}") @Nullable String defaultToName,
            @Value("${simplejavamail.defaults.to.address:#{null}}") @Nullable String defaultToAddress,
            @Value("${simplejavamail.defaults.cc.name:#{null}}") @Nullable String defaultCcName,
            @Value("${simplejavamail.defaults.cc.address:#{null}}") @Nullable String defaultCcAddress,
            @Value("${simplejavamail.defaults.bcc.name:#{null}}") @Nullable String defaultBccName,
            @Value("${simplejavamail.defaults.bcc.address:#{null}}") @Nullable String defaultBccAddress,
            @Value("${simplejavamail.defaults.poolsize:#{null}}") @Nullable String defaultPoolsize,
            @Value("${simplejavamail.defaults.poolsize.keepalivetime:#{null}}") @Nullable String defaultPoolKeepAlivetime,
            @Value("${simplejavamail.defaults.connectionpool.clusterkey.uuid:#{null}}") @Nullable String defaultConnectionPoolCluterKey,
            @Value("${simplejavamail.defaults.connectionpool.coresize:#{null}}") @Nullable String defaultConnectionPoolCoreSize,
            @Value("${simplejavamail.defaults.connectionpool.maxsize:#{null}}") @Nullable String defaultConnectionPoolMaxSize,
            @Value("${simplejavamail.defaults.connectionpool.claimtimeout.millis:#{null}}") @Nullable String defaultConnectionPoolClaimTimeoutMillis,
            @Value("${simplejavamail.defaults.connectionpool.expireafter.millis:#{null}}") @Nullable String defaultConnectionPoolExpireAfterMillis,
            @Value("${simplejavamail.defaults.connectionpool.loadbalancing.strategy:#{null}}") @Nullable String defaultConnectionPoolLoadBalancingStrategy,
            @Value("${simplejavamail.defaults.sessiontimeoutmillis:#{null}}") @Nullable String defaultSessionTimeoutMillis,
            @Value("${simplejavamail.defaults.trustallhosts:#{null}}") @Nullable String defaultTrustAllHosts,
            @Value("${simplejavamail.defaults.trustedhosts:#{null}}") @Nullable String defaultTrustedHosts,
            @Value("${simplejavamail.defaults.verifyserveridentity:#{null}}") @Nullable String defaultVerifyServerIdentity,
            @Value("${simplejavamail.transport.mode.logging.only:#{null}}") @Nullable String defaultTransportModeLoggingOnly,
            @Value("${simplejavamail.opportunistic.tls:#{null}}") @Nullable String defaultOpportunisticTls,
            @Value("${simplejavamail.smime.signing.keystore:#{null}}") @Nullable String smimeSigningKeyStore,
            @Value("${simplejavamail.smime.signing.keystore_password:#{null}}") @Nullable String smimeSigningKeyStorePassword,
            @Value("${simplejavamail.smime.signing.key_alias:#{null}}") @Nullable String smimeSigningKeyAlias,
            @Value("${simplejavamail.smime.signing.key_password:#{null}}") @Nullable String smimeSigningKeyPassword,
            @Value("${simplejavamail.smime.encryption.certificate:#{null}}") @Nullable String smimeEncryptionCertificate,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.enable.dir:#{null}}") @Nullable String embeddedimagesDynamicresolutionEnableDir,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.enable.url:#{null}}") @Nullable String embeddedimagesDynamicresolutionEnableUrl,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.enable.classpath:#{null}}") @Nullable String embeddedimagesDynamicresolutionEnableClassPath,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.base.dir:#{null}}") @Nullable String embeddedimagesDynamicresolutionBaseDir,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.base.url:#{null}}") @Nullable String embeddedimagesDynamicresolutionBaseUrl,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.base.classpath:#{null}}") @Nullable String embeddedimagesDynamicresolutionBaseClassPath,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.outside.base.dir:#{null}}") @Nullable String embeddedimagesDynamicresolutionOutsideBaseDir,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.outside.base.classpath:#{null}}") @Nullable String embeddedimagesDynamicresolutionOutsideBaseClassPath,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.outside.base.url:#{null}}") @Nullable String embeddedimagesDynamicresolutionOutsideBaseUrl,
            @Value("${simplejavamail.embeddedimages.dynamicresolution.mustbesuccesful:#{null}}") @Nullable String embeddedimagesDynamicresolutionMustBeSuccesful
    ) {
        Properties properties = new Properties();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(mailProperties.getProtocol()).to(value -> properties.setProperty(ConfigLoader.Property.TRANSPORT_STRATEGY.key(), value.toUpperCase(Locale.ROOT)));
        mapper.from(mailProperties.getHost()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_HOST.key(), value));
        mapper.from(mailProperties.getPort()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_PORT.key(), String.valueOf(value)));
        mapper.from(mailProperties.getUsername()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_USERNAME.key(), value));
        mapper.from(mailProperties.getPassword()).to(value -> properties.setProperty(ConfigLoader.Property.SMTP_PASSWORD.key(), value));
        ConfigLoader.loadProperties(properties, true);

        return super.loadGlobalConfigAndCreateDefaultMailer(
                javaxmailDebug,
                transportstrategy,
                smtpHost,
                smtpPort,
                smtpUsername,
                smtpPassword,
                customSSLFactoryClass,
                proxyHost,
                proxyPort,
                proxyUsername,
                proxyPassword,
                proxySocks5bridgePort,
                defaultSubject,
                defaultFromName,
                defaultFromAddress,
                defaultReplytoName,
                defaultReplytoAddress,
                defaultBouncetoName,
                defaultBouncetoAddress,
                defaultToName,
                defaultToAddress,
                defaultCcName,
                defaultCcAddress,
                defaultBccName,
                defaultBccAddress,
                defaultPoolsize,
                defaultPoolKeepAlivetime,
                defaultConnectionPoolCluterKey,
                defaultConnectionPoolCoreSize,
                defaultConnectionPoolMaxSize,
                defaultConnectionPoolClaimTimeoutMillis,
                defaultConnectionPoolExpireAfterMillis,
                defaultConnectionPoolLoadBalancingStrategy,
                defaultSessionTimeoutMillis,
                defaultTrustAllHosts,
                defaultTrustedHosts,
                defaultVerifyServerIdentity,
                defaultTransportModeLoggingOnly,
                defaultOpportunisticTls,
                smimeSigningKeyStore,
                smimeSigningKeyStorePassword,
                smimeSigningKeyAlias,
                smimeSigningKeyPassword,
                smimeEncryptionCertificate,
                embeddedimagesDynamicresolutionEnableDir,
                embeddedimagesDynamicresolutionEnableUrl,
                embeddedimagesDynamicresolutionEnableClassPath,
                embeddedimagesDynamicresolutionBaseDir,
                embeddedimagesDynamicresolutionBaseUrl,
                embeddedimagesDynamicresolutionBaseClassPath,
                embeddedimagesDynamicresolutionOutsideBaseDir,
                embeddedimagesDynamicresolutionOutsideBaseClassPath,
                embeddedimagesDynamicresolutionOutsideBaseUrl,
                embeddedimagesDynamicresolutionMustBeSuccesful
        );
    }

    static class SESCondition extends AnyNestedCondition {

        SESCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = "host")
        static class SpringMailHostProperty {
        }

        @ConditionalOnProperty(prefix = "simplejavamail.smtp", name = "host")
        static class SimpleJavaMailHostProperty {
        }
    }
}
