package com.ethan.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.UUID.randomUUID;

/**
 * https://stackoverflow.com/questions/46154994/how-to-log-spring-5-webclient-call/55790675#55790675
 */
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingFilterFunction implements ExchangeFilterFunction {
    private static final int MAX_BYTES_LOGGED = 4_096;

    private final String externalSystem;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // if (!log.isDebugEnabled()) {
        //     return next.exchange(request);
        // }
        var clientRequestId = randomUUID().toString();

        var requestLogged = new AtomicBoolean(false);
        var responseLogged = new AtomicBoolean(false);

        var capturedRequestBody = new StringBuilder();
        var capturedResponseBody = new StringBuilder();

        var stopWatch = new StopWatch();
        stopWatch.start();

        return next
                .exchange(ClientRequest.from(request).body((req, context) ->
                        request.body().insert(new ClientHttpRequestDecorator(req) {
                            @Override
                            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                                return super.writeWith(Flux.from(body).doOnNext(data -> capturedRequestBody.append(extractBytes(data)))); // number of bytes appended is maxed in real code
                            }
                        }, context)
                ).build())
                // .exchange(ClientRequest.from(request).body(new BodyInserter<>() {
                //     @Override
                //     public Mono<Void> insert(ClientHttpRequest req, Context context) {
                //
                //         return request.body().insert(new ClientHttpRequestDecorator(req) {
                //
                //             @Override
                //             public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                //                 return super.writeWith(Flux.from(body).doOnNext(data -> capturedRequestBody.append(extractBytes(data)))); // number of bytes appended is maxed in real code
                //             }
                //
                //         }, context);
                //     }
                // }).build())
                .doOnNext(response -> {
                            if (!requestLogged.getAndSet(true)) {
                                log.info("| >>---> Outgoing {} request [{}]\n{} {}\n{}\n\n{}\n",
                                        externalSystem,
                                        clientRequestId,
                                        request.method(),
                                        request.url(),
                                        request.headers(), // filtered in real code
                                        capturedRequestBody // filtered in real code
                                );
                            }
                        }
                )
                .doOnError(error -> {
                    if (!requestLogged.getAndSet(true)) {
                        log.info("| >>---> Error Outgoing {} request [{}]\n{} {}\n{}\n\nError: {}\n",
                                externalSystem,
                                clientRequestId,
                                request.method(),
                                request.url(),
                                request.headers(),
                                error
                        );
                    }
                })
                .map(response -> response.mutate().body(transformer -> transformer
                                .doOnNext(body -> capturedResponseBody.append(extractBytes(body))) // number of bytes appended is maxed in real code
                                .doOnTerminate(() -> {
                                    if (stopWatch.isRunning()) {
                                        stopWatch.stop();
                                    }
                                })
                                .doOnComplete(() -> {
                                    if (!responseLogged.getAndSet(true)) {
                                        log.info("| <---<< Response for outgoing {} request [{}] after {}ms\n{} {}\n{}\n{}",
                                                externalSystem,
                                                clientRequestId,
                                                stopWatch.getTotalTimeMillis(),
                                                response.statusCode().value(),
                                                response.statusCode(),
                                                response.headers().asHttpHeaders(), // HttpHeaders implement toString() with formatting, filtered in real code
                                                capturedResponseBody.toString() // filtered in real code
                                        );
                                    }
                                })
                                .doOnError(error -> {
                                            if (!responseLogged.getAndSet(true)) {
                                                log.info("| <---<< Error parsing response for outgoing {} request [{}] after {}ms\n{}",
                                                        externalSystem,
                                                        clientRequestId,
                                                        stopWatch.getTotalTimeMillis(),
                                                        error.getMessage()
                                                );
                                            }
                                        }
                                )
                        ).build()
                );
    }


    private static String extractBytes(DataBuffer data) {
        int currentReadPosition = data.readPosition();
        var numberOfBytesLogged = Math.min(data.readableByteCount(), MAX_BYTES_LOGGED);
        var bytes = new byte[numberOfBytesLogged];
        data.read(bytes, 0, numberOfBytesLogged);
        data.readPosition(currentReadPosition);
        return new String(bytes);
    }
}
