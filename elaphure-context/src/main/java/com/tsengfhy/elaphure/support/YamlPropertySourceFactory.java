package com.tsengfhy.elaphure.support;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * Throw out {@link FileNotFoundException} when resource doesn't exists, and then {@link org.springframework.context.annotation.PropertySource#ignoreResourceNotFound} will take effect normally.
     * Or {@link org.springframework.context.annotation.PropertySource#ignoreResourceNotFound} will not take effect as FileNotFoundException is wrapped by {@link java.lang.IllegalStateException}
     *
     * @see org.springframework.beans.factory.config.YamlProcessor#handleProcessError
     * @see org.springframework.context.annotation.ConfigurationClassParser#processPropertySource
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        Resource originalResource = Optional.of(resource.getResource()).filter(Resource::exists).orElseThrow(FileNotFoundException::new);
        String propertyName = Optional.ofNullable(name).orElseGet(originalResource::getFilename);
        return new YamlPropertySourceLoader()
                .load(propertyName, originalResource)
                .stream()
                .findFirst()
                .orElseGet(() -> new PropertySource.StubPropertySource(propertyName));
    }
}
