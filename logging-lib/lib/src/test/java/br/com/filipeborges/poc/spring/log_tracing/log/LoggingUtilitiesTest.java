package br.com.filipeborges.poc.spring.log_tracing.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;

public class LoggingUtilitiesTest {

    private Consumer<Integer> nextConsumer;
    private Consumer<Throwable> errorConsumer;
    private Runnable completeRunnable;
    private TestPublisher<Integer> testPublisher;

    @BeforeEach
    public void setUp() {
        nextConsumer = Mockito.mock(Consumer.class);
        errorConsumer = Mockito.mock(Consumer.class);
        completeRunnable = Mockito.mock(Runnable.class);
        testPublisher = TestPublisher.create();
    }

    @Test
    public void shouldLogOnNextWhenNextSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnNext(nextConsumer));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.emit(1))
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(nextConsumer, Mockito.times(1)).accept(any());
    }

    @Test
    public void shouldNotLogOnNextWhenErrorSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnNext(nextConsumer));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.error(new Exception()))
                .verifyError();

        Mockito.verify(nextConsumer, Mockito.never()).accept(any());
    }

    @Test
    public void shouldLogOnErrorWhenErrorSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnError(errorConsumer));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.error(new Exception()))
                .verifyError();

        Mockito.verify(errorConsumer, Mockito.times(1)).accept(any());
    }

    @Test
    public void shouldNotLogOnErrorWhenNotErrorSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnNext(nextConsumer));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.emit(1))
                .expectNextCount(1)
                .verifyComplete();

        Mockito.verify(errorConsumer, Mockito.never()).accept(any());
    }

    @Test
    public void shouldLogOnCompleteWhenCompleteSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnComplete(completeRunnable));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.complete())
                .verifyComplete();

        Mockito.verify(completeRunnable, Mockito.times(1)).run();
    }

    @Test
    public void shouldNotLogOnCompleteWhenErrorSignal() {
        var publisher = testPublisher.mono()
                .doOnEach(LoggingUtilities.logOnComplete(completeRunnable));

        StepVerifier.create(publisher)
                .then(() -> testPublisher.error(new Exception()))
                .verifyError();

        Mockito.verify(completeRunnable, Mockito.never()).run();
    }

}
