package com.orderms.order_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class FeignConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public RequestInterceptor correlationIdRequestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate template) {

                String correlationId = MDC.get("correlationId");

                if(correlationId!=null && !correlationId.isBlank()) {
                    template.header("X-Correlation-Id", correlationId);

                    log.info(">>> FEIGN HEADER ADDED: X-Correlation-Id={}", correlationId);
                }


            }
        };
    }
}
