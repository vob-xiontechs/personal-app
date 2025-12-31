package com.dev.backendapi.config;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.dev.backendapi.exception.BaseException;

import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Mono;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.DateTime);
    }

    @Bean
    public DataFetcherExceptionResolver dataFetcherExceptionResolver() {
        return (ex, env) -> {
            if (ex instanceof BaseException) {
                BaseException baseEx = (BaseException) ex;

                GraphQLError error = GraphqlErrorBuilder.newError()
                    .message(baseEx.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of(
                        "errorCode", baseEx.getErrorCode(),
                        "layer", baseEx.getLayer(),
                        "classification", "CUSTOM_ERROR"
                    ))
                    .build();

                return Mono.just(java.util.List.of(error));
            }

            // For other exceptions, return generic error
            GraphQLError error = GraphqlErrorBuilder.newError()
                .message("Internal server error")
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(java.util.Map.of(
                    "classification", "INTERNAL_ERROR"
                ))
                .build();

            return Mono.just(java.util.List.of(error));
        };
    }
}
