package br.com.filipeborges.poc.spring.log_tracing.log.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;

@Configuration
public class CustomLogAutoConfiguration {
    //https://docs.spring.io/spring-framework/docs/5.3.5/javadoc-api/org/springframework/web/filter/reactive/ServerWebExchangeContextFilter.html
    @ConditionalOnMissingBean
    @Bean
    public ServerWebExchangeContextFilter addServerWebExchangeToReactorContext() {
        return new ServerWebExchangeContextFilter();
    }
}
