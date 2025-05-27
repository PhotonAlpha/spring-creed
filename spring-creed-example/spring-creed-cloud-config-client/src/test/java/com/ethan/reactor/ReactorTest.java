package com.ethan.reactor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import reactor.core.observability.SignalListenerFactory;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 19/5/25
 */
@Slf4j
public class ReactorTest {
    @Test
    void subscribeTest() throws InterruptedException {
        CountDownLatch count = new CountDownLatch(1);
        Scheduler s1 = Schedulers.newParallel("scheduler-A", 4);
        Scheduler s2 = Schedulers.newParallel("scheduler-B", 4);
        Flux.range(1, 2)
                .map(i -> { log.info("mapA:{}", i); return i; })
                .subscribeOn(s1)
                .map(i -> { log.info("mapB:{}", i); return i; })
                .publishOn(s2)
                .map(i -> { log.info("mapC:{}", i); return i; })
                .subscribe(i->log.info("subscribe:{}", i),
                        t -> {
                            count.countDown();
                            log.info("t:{}", t);
                        },
                        count::countDown);
        count.await();

    }

    public Mono<String> addPrefix(int val) {
        return Mono.just("prefix_" + val)
                .doOnNext(s -> log.info("addPrefix:" + s));
    }


    @Test
    void testFlatMap() throws Exception {
        CountDownLatch count = new CountDownLatch(1);
        Flux.range(1, 3)
                .flatMap(i -> addPrefix(i)
                        .subscribeOn(Schedulers.parallel()))
                .subscribe(
                        s -> log.info("main:" + s),
                        t -> count.countDown(),
                        count::countDown);
        count.await();
    }

    @Test
    void defer_test() {

        //声明阶段创建DeferClass对象
        Mono<Date> m1 = Mono.just(new Date());
        Mono<Date> m2 = Mono.defer(()->Mono.just(new Date()));

        m1.subscribe(System.out::println);
        m2.subscribe(System.out::println);
        //延迟5秒钟
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m1.subscribe(System.out::println);
        m2.subscribe(System.out::println);

    }
    private boolean getForEntity() {
        log.info("{}", "alive");
        return true;
    }

    @Test
    void doOnNext_test() {

        Flux<String> flux = Flux.just("Spring", "Boot", "3", LocalDateTime.now() + "")
                .doOnNext(value -> System.out.println("Processing value: " + value))
                .map(String::toUpperCase);
        System.out.println("==");
        flux.subscribe(System.out::println);
        System.out.println("==");
        flux.subscribe(System.out::println);
    }

    @Test
    void doOnNext_test2() throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        Function<ServiceInstance, Consumer<Boolean>> listener = serviceInstance -> value ->
                log.info("listener {} alive? {}", serviceInstance.getInstanceId(), value);
        ServiceInstance serviceInstance = new DefaultServiceInstance();
        LoggingMeterRegistry registry = new LoggingMeterRegistry();
        SignalListenerFactory<Boolean, ?> metrics = Micrometer.metrics(
                registry
        );
        Mono<Boolean> m3 = Mono.defer(() -> {
            Mono<Boolean> alive;
            try {
                alive = Mono.just(getForEntity());
                log.info("just {}", LocalDateTime.now());
            } catch (Exception ignored) {
                alive = Mono.just(false);
            }
            // alive.subscribe(re -> log.info("re {}", re));
            return alive
                    .name("test1")
                    .tag("ke", "va")
                    .tap(metrics)
                    // .doOnNext(event -> log.info("Received {}", event))
                    .doOnSubscribe(sub -> System.out.println("Subscription started " + sub))
                    .doFinally(typ -> System.out.println("doFinally started " + typ.toString()))
                    ;
        }).doOnNext(event -> log.info("Received {}", event));
        // Micrometer.observation( registry, Function<ObservationRegistry, Observation> observationSupplier)

        // m3.subscribe(System.out::println);
        log.info("main {}", LocalDateTime.now());
        m3.subscribe(System.out::println);
    }

    @Test
    void testScheduler() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Scheduler scheduler = Schedulers.fromExecutorService(executorService);
        Flux.create(sink -> {

                    sink.next(Thread.currentThread().getName());

                    sink.complete();

                })

                .publishOn(Schedulers.single())

                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))

                .publishOn(Schedulers.newBoundedElastic(10, 100, "th-"))

                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), x))
                .checkpoint("test")
                .subscribeOn(Schedulers.parallel())

                .toStream()

                .forEach(System.out::println);


        Flux.just(1, 0).map(x -> 1 / x).checkpoint("test").subscribe(System.out::println);

        System.out.println("==");
        Flux.range(1, 10).log("Range").subscribe(System.out::println);
    }
    private Consumer<Object> objectConsumer() {
        return obj -> log.info("subscribe:{}", obj);
    }


    @Test
    void take_test() {
        Flux<Integer> integerFlux = Flux.range(1, 1000).take(10);
        // integerFlux
        //         .subscribe(objectConsumer());
        integerFlux.toStream().forEach(System.out::println);
        System.out.println("==");
        Flux.range(1, 1000).takeLast(10).subscribe(objectConsumer());
        System.out.println("==");
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(objectConsumer());
        System.out.println("==");
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(objectConsumer());
    }

    @Test
    void testConnectableFlux() throws InterruptedException {
        final Flux<Long> source = Flux.interval(Duration.ofMillis(1000))
                .take(10) //原始的序列中包含 10 个间隔为 1 秒的元素
                .publish() // 通过 publish()方法把一个 Flux 对象转换成 ConnectableFlux 对象
                .autoConnect(); // autoConnect()的作用是当 ConnectableFlux 对象有一个订阅者时就开始产生消息
        source.subscribe(objectConsumer());
        Thread.sleep(5000);
        source
                .toStream() //  toStream()方法把 Flux 序列转换成 Java 8 中的 Stream 对象，再通过 forEach()方法来进行输出。
                .forEach(objectConsumer());
    }

    @Test
    void repetTest() throws InterruptedException {
        log.info("start");
        // Flux.just(1, 2, 3)
        //         .delaySubscription(Duration.ofSeconds(1))
        //         .subscribe(objectConsumer());
        Flux.just(1, 2, 3)
                .delaySubscription(Duration.ZERO)
                .replay(1)
                .refCount(1)
                .subscribe(objectConsumer());
        // Flux.just(1, 2, 3)
        //         .repeatWhen(Retry.fixedDelay(2L, Duration.ofSeconds(1)))
        //         .subscribe(objectConsumer());
        // TimeUnit.SECONDS.sleep(10);

        System.out.println(DurationStyle.detectAndParse("10s"));
    }
}
