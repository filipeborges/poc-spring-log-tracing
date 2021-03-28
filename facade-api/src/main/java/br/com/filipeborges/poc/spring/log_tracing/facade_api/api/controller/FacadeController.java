package br.com.filipeborges.poc.spring.log_tracing.facade_api.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

@RestController
public class FacadeController {

    @GetMapping("/calculate")
    public Mono<Void> calculate() {
        return Mono.just(true)
                .transformDeferredContextual(this::printTransactionIdRequestHeader)
                .then();
    }

    // TODO: Move to WebFilter
    // https://projectreactor.io/docs/core/release/reference/#context
    private <T> Mono<T> printTransactionIdRequestHeader(Mono<T> upstreamMono, ContextView contextView) {
        return Mono.just((ServerWebExchange)contextView.get(ServerWebExchangeContextFilter.EXCHANGE_CONTEXT_ATTRIBUTE))
                .map(serverWebExchange -> serverWebExchange.getRequest().getHeaders().get("TransactionID").get(0))
                .doOnNext(tid -> System.out.println("=========> TID From Header: " + tid))
                .flatMap(_s -> upstreamMono)
                .switchIfEmpty(upstreamMono);
    }

}