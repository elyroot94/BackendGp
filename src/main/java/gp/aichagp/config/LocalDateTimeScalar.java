
package gp.aichagp.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import graphql.language.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocalDateTimeScalar {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static GraphQLScalarType createLocalDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Custom scalar for handling LocalDateTime in format 'yyyy-MM-ddTHH:mm'")
                .coercing(new Coercing<LocalDateTime, String>() {

                    @Override
                    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale) {
                        if (dataFetcherResult instanceof LocalDateTime dateTime) {
                            return FORMATTER.format(dateTime);
                        }
                        throw new CoercingSerializeException("Expected a LocalDateTime object.");
                    }

                    @Override
                    public LocalDateTime parseValue(Object input, GraphQLContext context, Locale locale) {
                        if (input instanceof String dateTimeStr) {
                            try {
                                return LocalDateTime.parse(dateTimeStr, FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException(
                                        "Invalid LocalDateTime format. Expected"+ FORMATTER, e);
                            }
                        }
                        throw new CoercingParseValueException("Expected a String value for LocalDateTime.");
                    }

                    @Override
                    public LocalDateTime parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext context, Locale locale) {
                        if (input instanceof StringValue stringValue) {
                            try {
                                return LocalDateTime.parse(stringValue.getValue(), FORMATTER);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(
                                        "Invalid LocalDateTime literal. Expected "+ FORMATTER, e);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected a StringValue for LocalDateTime literal.");
                    }
                }).build();
    }
}
