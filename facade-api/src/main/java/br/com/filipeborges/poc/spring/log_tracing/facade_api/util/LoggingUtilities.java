package br.com.filipeborges.poc.spring.log_tracing.facade_api.util;

import org.slf4j.MDC;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Signal;

import java.util.Optional;
import java.util.function.Consumer;

public class LoggingUtilities {

    private static final String MDC_TID_KEY = "transactionId";

    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logConsumer) {
        return signal -> {
            if (!signal.isOnNext()) return;
            getTransactionId(signal)
                    .ifPresentOrElse(
                            tid -> {
                                try (MDC.MDCCloseable mdc = MDC.putCloseable(MDC_TID_KEY, tid)) {
                                    logConsumer.accept(signal.get());
                                }
                            },
                            () -> logConsumer.accept(signal.get())
                    );
        };
    }

    public static <T> Consumer<Signal<T>> logOnError(Consumer<Throwable> logConsumer) {
        return signal -> {
            if (!signal.isOnError()) return;
            getTransactionId(signal)
                    .ifPresentOrElse(
                            tid -> {
                                try (MDC.MDCCloseable mdc = MDC.putCloseable(MDC_TID_KEY, tid)) {
                                    logConsumer.accept(signal.getThrowable());
                                }
                            },
                            () -> logConsumer.accept(signal.getThrowable())
                    );
        };
    }

    public static <T> Consumer<Signal<T>> logOnComplete(Runnable logRunnable) {
        return signal -> {
            if (!signal.isOnComplete()) return;
            getTransactionId(signal)
                    .ifPresentOrElse(
                            tid -> {
                                try (MDC.MDCCloseable mdc = MDC.putCloseable(MDC_TID_KEY, tid)) {
                                    logRunnable.run();
                                }
                            },
                            logRunnable
                    );
        };
    }

    private static <T> Optional<String> getTransactionId(Signal<T> signal) {
        return signal.getContextView().getOrEmpty(ServerWebExchangeContextFilter.EXCHANGE_CONTEXT_ATTRIBUTE)
                .map(
                        serverWebExchange -> ((ServerWebExchange)serverWebExchange).getRequest().getHeaders().get("TransactionID")
                )
                .map(values -> values.get(0));
    }

}
