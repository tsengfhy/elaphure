package com.tsengfhy.entry.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
public class TestQueue {

    public static final String OUT = "test-out-0";
    public static final String IN = "test-in-0";

    @Bean
    public Consumer<String> test() {
        return log::info;
    }
}
