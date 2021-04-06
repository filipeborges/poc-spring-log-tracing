package br.com.filipeborges.poc.spring.log_tracing.facade_api.api.controller;

import br.com.filipeborges.poc.spring.log_tracing.facade_api.util.LoggingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Optional;

@RestController
public class FacadeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacadeController.class);

    @GetMapping("/calculate")
    public Mono<Void> calculate() {
        return Mono.just(true)
//                .transformDeferredContextual(this::logRequestContextTransformation)
                .flatMap(bool -> Mono.error(new RuntimeException("FAIL!!!")))
                .doOnEach(LoggingUtilities.logOnError(err -> LOGGER.error("Error", err)))
                .onErrorReturn(true)
                .doOnEach(LoggingUtilities.logOnNext(bool -> LOGGER.info("Emmited value: {}", bool)))
                .doOnEach(LoggingUtilities.logOnComplete(() -> LOGGER.info("Completed operation")))
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