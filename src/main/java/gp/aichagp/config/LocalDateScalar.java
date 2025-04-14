
package gp.aichagp.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Component
public class LocalDateScalar {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static GraphQLScalarType createLocalDateScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDate")
                .description("Custom scalar for handling LocalDate in format 'yyyy-MM-dd'")
                .coercing(new Coercing<LocalDate, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale) {
                        if (dataFetcherResult instanceof LocalDate date) {
                            return FORMATTER.format(date);
                        }
                        throw new CoercingSerializeException("Expected a LocalDate object.");
                    }

                    @Override
                    public LocalDate parseValue(Object input, GraphQLContext context, Locale locale) {
                        if (input instanceof String dateStr) {
                            try {
                                return LocalDate.parse(dateStr, FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException(
                                        "Invalid LocalDate format. Expected 'yyyy-MM-dd'.", e);
                            }
                        }
                        throw new CoercingParseValueException("Expected a String value for LocalDate.");
                    }

                    @Override
                    public LocalDate parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext context, Locale locale) {
                        if (input instanceof StringValue stringValue) {
                            try {
                                return LocalDate.parse(stringValue.getValue(), FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(
                                        "Invalid LocalDate literal. Expected 'yyyy-MM-dd'.", e);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected a StringValue for LocalDate literal.");
                    }
                }).build();
    }
}


