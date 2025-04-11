package gp.aichagp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import graphql.schema.GraphQLScalarType;
import graphql.schema.Coercing;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalar());
    }

    private GraphQLScalarType localDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Custom DateTime scalar")
                .coercing(new Coercing<LocalDateTime, String>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                    @Override
                    public String serialize(Object input) {
                        if (input == null) {
                            return null;
                        }
                        if (input instanceof LocalDateTime dateTime) {
                            return dateTime.format(formatter);
                        } else if (input instanceof String string) {
                            // Cas où l'entité aurait gardé la date en String
                            return string;
                        }
                        throw new IllegalArgumentException("Value must be LocalDateTime or ISO String");
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        if (input instanceof String string) {
                            try {
                                return LocalDateTime.parse(string, formatter);
                            } catch (DateTimeParseException e) {
                                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd'T'HH:mm:ss");
                            }
                        }
                        throw new IllegalArgumentException("Value must be a String");
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) {
                        return parseValue(input);
                    }
                })
                .build();
    }
}