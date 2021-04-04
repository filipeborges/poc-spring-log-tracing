package br.com.filipeborges.poc.spring.log_tracing.facade_api.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Optional;

@RestController
public class FacadeController {

    @GetMapping("/calculate")
    public Mono<Void> calculate() {
        return Mono.just(true)
                .transformDeferredContextual(this::logRequestContextTransformation)
                .then();
    }

    // https://projectreactor.io/docs/core/release/reference/#context
    private <T> Mono<T> logRequestContextTransformation(Mono<T> upstreamMono, ContextView contextView) {
        return Optional.of((ServerWebExchange)contextView.get(ServerWebExchangeContextFilter.EXCHANGE_CONTEXT_ATTRIBUTE))
                .map(serverWebExchange -> serverWebExchange.getRequest().getHeaders().get("TransactionID"))
                .map(values -> values.get(0))
                .map(tid -> "=========> TransactionID: " + tid)
                .map(upstreamMono::log)
                .orElse(upstreamMono);
    }

}