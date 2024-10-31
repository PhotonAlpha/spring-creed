package com.ethan;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxTest {
    @Test
    void fluxgenerate() {
        Flux.generate(() -> 1, (i, sink) -> {
            sink.next(i);
            if (i == 5) {
                sink.complete();
            }
            return ++i;
        }).subscribe(System.out::println);

        Flux.create(sink -> {
            for (int i = 0; i < 5; i++) {
                sink.next("javaedge" + i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    @Test
    void name() throws InterruptedException {
        Repeat<Object> aliveInstancesReplayRepeat = Repeat
                .onlyIf(repeatContext -> true)
                .fixedBackoff(Duration.ofSeconds(10));
        Flux<String> stringFlux = Flux.defer(() -> Flux.range(1, 5))
                .repeatWhen(aliveInstancesReplayRepeat)
                .switchMap(val -> Mono.just("Hello" + val));

        Flux<String> source = stringFlux
                .delaySubscription(Duration.ofSeconds(3))
                .replay(1)
                .refCount(1);

                // .;
                // .autoConnect();
        log.info("-----");
        source.subscribe((a) -> log.info("{}",a));
        source.subscribe((a) -> log.info("{}",a));
        TimeUnit.SECONDS.sleep(10);

    }

    @Test
    void apiTesting() {
        Mono<String> monoA = Mono.fromSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "ResultA";
        });
        Mono<String> monoB = Mono.fromSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "ResultB";
        });
        Mono<String> monoC = Mono.fromSupplier(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "ResultC";
        });

        Mono<Tuple3<String, String, String>> finalMono = Mono.zip(monoA, monoB)
                .flatMap(ab -> {
                    return monoC.map(c -> Tuples.of(ab.getT1(), ab.getT2(), c));
                });
        finalMono.subscribe(System.out::println);
    }

    @Test
    void bigDec() {
        long base = 12345;
        int scale = 4;

        BigDecimal number = BigDecimal.valueOf(base, scale);
        System.out.println(number);
        BigDecimal pointRight = number.movePointRight(5);
        System.out.println(pointRight + "; my scale is " + pointRight.scale());
        BigDecimal scaleBy = number.scaleByPowerOfTen(5);
        System.out.println(scaleBy + "; my scale is " + scaleBy.scale());


        System.out.println(new BigDecimal("15").movePointLeft(2));
    }

    /**
     * 异常情况的处理
     */
    @Test
    void onError() {
        // Flux.range(1,5)
        //         .doOnNext(i -> System.out.println("input=" + i))
        //         .map(i -> i == 2 ? i / 0 : i)
        //         .map(i -> i * 2)
        //         .reduce((i,j) -> i+j)
        //         .doOnNext(i -> System.out.println("sum=" + i))
        //         .block();


        Flux.range(1,5)
                .doOnNext(i -> System.out.println("input=" + i))
                .map(i -> i == 2 ? i / 0 : i)
                .map(i -> i * 2)
                .onErrorResume(err -> {
                    log.info("onErrorResume", err);
                    return Flux.empty();
                })
                .reduce((i,j) -> i+j)
                .doOnNext(i -> System.out.println("sum=" + i))
                .block();
        System.out.println("----------split-------");
        Flux.range(1,5)
                .doOnNext(i -> System.out.println("input=" + i))
                .map(i -> i == 2 ? i / 0 : i)
                .map(i -> i * 2)
                .onErrorContinue((err, i) -> {
                    log.info("onErrorResume i={}", i, err);
                })
                .reduce((i,j) -> i+j)
                .doOnNext(i -> System.out.println("sum=" + i))
                .block();
        System.out.println("----------split-------");
        Flux.range(1,5)
                .doOnNext(i -> System.out.println("input=" + i))
                .map(i -> i == 2 ? i / 0 : i)
                .map(i -> i * 2)
                .onErrorComplete()
                .reduce((i,j) -> i+j)
                .doOnNext(i -> System.out.println("sum=" + i))
                .block();
    }
}
