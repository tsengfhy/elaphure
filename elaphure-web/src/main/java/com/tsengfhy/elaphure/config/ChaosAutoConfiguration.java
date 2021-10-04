package com.tsengfhy.elaphure.config;

import com.tsengfhy.elaphure.support.YamlPropertySourceFactory;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyConfiguration;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;

@AutoConfiguration(before = ChaosMonkeyConfiguration.class)
@ConditionalOnProperty(name = "chaos.monkey.enabled", matchIfMissing = true)
@PropertySource(value = "classpath:elaphure-chaos.yml", factory = YamlPropertySourceFactory.class)
class ChaosAutoConfiguration extends ChaosMonkeyConfiguration {

    ChaosAutoConfiguration(ChaosMonkeyProperties chaosMonkeyProperties, WatcherProperties watcherProperties, AssaultProperties assaultProperties) {
        super(chaosMonkeyProperties, watcherProperties, assaultProperties);
    }
}
