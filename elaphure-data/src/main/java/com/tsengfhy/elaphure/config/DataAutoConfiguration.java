package com.tsengfhy.elaphure.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusInnerInterceptorAutoConfiguration;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.tsengfhy.elaphure.data.AutoFillMetaObjectHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = MybatisPlusAutoConfiguration.class, before = MybatisPlusInnerInterceptorAutoConfiguration.class)
@ConditionalOnBean(MybatisPlusAutoConfiguration.class)
class DataAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    PaginationInnerInterceptor paginationInnerInterceptor() {
        return new PaginationInnerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    MetaObjectHandler metaObjectHandler() {
        return new AutoFillMetaObjectHandler();
    }
}
