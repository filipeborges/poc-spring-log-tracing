package br.com.filipeborges.poc.spring.log_tracing.facade_api.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class ApiConfiguration {

    //https://docs.spring.io/spring-framework/docs/5.3.5/javadoc-api/org/springframework/web/filter/reactive/ServerWebExchangeContextFilter.html
    @Bean
    public ServerWebExchangeContextFilter addServerWebExchangeToReactorContext() {
        return new ServerWebExchangeContextFilter();
    }

    @Bean
    public WebFilter requestContextLoggingFilter() {
        final Logger logger = LoggerFactory.getLogger("CustomWebFilter");

        return new WebFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                Optional.ofNullable(exchange.getRequest().getHeaders().get("TransactionID"))
                        .map(values -> values.get(0))
                        .ifPresent(tid -> logger.info("=======> TransactionID: " + tid));
                return chain.filter(exchange);
            }
        };
    }

}
