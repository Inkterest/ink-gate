package com.ink.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InkGateApplication {

    public static void main(String[] args) {
        SpringApplication.run(InkGateApplication.class, args);
    }


    // static imports from GatewayFilters and RoutePredicates
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ThrottleGatewayFilterFactory throttle) {
        return builder.routes()
                .route(r -> r.host("**.abc.org").and().path("/image/png")
                        .filters(f ->
                                f.addResponseHeader("X-TestHeader", "foobar"))
                        .uri("http://httpbin.org:80")
                )
                .route(r -> r.path("/image/webp")
                        .filters(f ->
                                f.addResponseHeader("X-AnotherHeader", "baz"))
                        .uri("http://httpbin.org:80")
                        .metadata("key", "value")
                )
                .route(r -> r.order(-1)
                        .host("**.throttle.org").and().path("/get")
                        .filters(f -> f.filter(throttle.apply(1,
                                1,
                                10,
                                TimeUnit.SECONDS)))
                        .uri("http://httpbin.org:80")
                        .metadata("key", "value")
                )
                .build();
    }
}
