package gp.aichagp.config;

import gp.aichagp.exceptions.DateFormatException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import graphql.schema.GraphQLScalarType;
import graphql.schema.Coercing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Bean
    public RouterFunction<ServerResponse> graphqlEndpoint(GraphQlHttpHandler httpHandler) {
        return RouterFunctions.route()
            .POST("/graphql", httpHandler::handleRequest)
            .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .type("Query", builder -> builder
                .dataFetcher("trajet", environment -> {
                    try {
                        return null; // Implémentation à venir
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
            )
            .scalar(DateTimeScalar);
    }

    public static final GraphQLScalarType DateTimeScalar = GraphQLScalarType.newScalar()
        .name("DateTime")
        .description("A date-time scalar")
        .coercing(new Coercing<LocalDateTime, String>() {
            @Override
            public String serialize(Object dataFetcherResult) {
                if (dataFetcherResult instanceof LocalDateTime) {
                    return ((LocalDateTime) dataFetcherResult).format(DATE_FORMATTER);
                }
                return null;
            }

            @Override
            public LocalDateTime parseValue(Object input) {
                if (input instanceof String) {
                    try {
                        return LocalDateTime.parse((String) input, DATE_FORMATTER);
                    } catch (Exception e) {
                        throw new DateFormatException("Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)");
                    }
                }
                return null;
            }

            @Override
            public LocalDateTime parseLiteral(Object input) {
                if (input instanceof String) {
                    try {
                        return LocalDateTime.parse((String) input, DATE_FORMATTER);
                    } catch (Exception e) {
                        throw new DateFormatException("Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)");
                    }
                }
                return null;
            }
        })
        .build();
} 