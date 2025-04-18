package gp.aichagp.unitaires;

import gp.aichagp.config.LocalDateTimeScalar;
import gp.aichagp.config.LocalDateScalar;
import graphql.GraphQLContext;
import graphql.language.StringValue;
import graphql.schema.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

 class CustomScalarTests {

    private GraphQLScalarType localDateScalar;
    private GraphQLScalarType localDateTimeScalar;
    private GraphQLContext context;
    private Locale locale;

    @BeforeEach
    void setUp() {
        localDateScalar = LocalDateScalar.createLocalDateScalar();
        localDateTimeScalar = LocalDateTimeScalar.createLocalDateTimeScalar();
        context = mock(GraphQLContext.class);
        locale = Locale.getDefault();
    }

    // Tests pour LocalDateScalar

    @Test
    void localDateScalar_serialize_validDate() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        assertEquals("2024-01-15", localDateScalar.getCoercing().serialize(date, context, locale));
    }

     @Test
     void localDateScalar_serialize_invalidType() {
         Coercing<?, ?> localDateCoercing = localDateScalar.getCoercing(); // Récupérer l'objet Coercing une seule fois
         assertThrows(CoercingSerializeException.class, () -> localDateCoercing.serialize("not a date", context, locale));
     }

    @Test
    void localDateScalar_parseValue_validString() {
        assertEquals(LocalDate.of(2023, 12, 31), localDateScalar.getCoercing().parseValue("2023-12-31", context, locale));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023/12/31", "2023-12", "31-12-2023", "invalid"})
    void localDateScalar_parseValue_invalidFormat(String input) {
        Coercing<?, ?> localDateCoercing = localDateScalar.getCoercing();
        CoercingParseValueException exception = assertThrows(CoercingParseValueException.class,
                () -> localDateCoercing.parseValue(input, context, locale));
        assertEquals("Invalid LocalDate format. Expected 'yyyy-MM-dd'.", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void localDateScalar_parseValue_invalidType() {
        Coercing<?, ?> localDateCoercing = localDateScalar.getCoercing();
        assertThrows(CoercingParseValueException.class, () -> localDateCoercing.parseValue(123, context, locale));
    }

    @Test
    void localDateScalar_parseLiteral_validStringValue() {
        StringValue input = new StringValue("2025-05-10");
        assertEquals(LocalDate.of(2025, 5, 10), localDateScalar.getCoercing().parseLiteral(input, null, context, locale));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2025/05/10", "2025-05", "10-05-2025", "oops"})
    void localDateScalar_parseLiteral_invalidFormat(String input) {
        Coercing<?, ?> localDateCoercing = localDateScalar.getCoercing();
        StringValue stringValue = new StringValue(input);
        CoercingParseLiteralException exception = assertThrows(CoercingParseLiteralException.class,
                () -> localDateCoercing.parseLiteral(stringValue, null, context, locale));
        assertEquals("Invalid LocalDate literal. Expected 'yyyy-MM-dd'.", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void localDateScalar_parseLiteral_invalidType() {
        Coercing<?, ?> localDateCoercing = localDateScalar.getCoercing();
        // Simuler un Value autre que StringValue (par exemple, IntValue)
        graphql.language.IntValue input = new graphql.language.IntValue(BigInteger.valueOf(123));
        assertThrows(CoercingParseLiteralException.class, () -> localDateCoercing.parseLiteral(input, null, context, locale));
    }

    // Tests pour LocalDateTimeScalar

    @Test
    void localDateTimeScalar_serialize_validDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        assertEquals(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime), localDateTimeScalar.getCoercing().serialize(dateTime, context, locale));
    }

    @Test
    void localDateTimeScalar_serialize_invalidType() {
        // Attempt to serialize a LocalDate as a LocalDateTime
        Object invalidType = LocalDate.now();
        Coercing<?, ?> localDateTimeCoercing = localDateTimeScalar.getCoercing();
        assertThrows(CoercingSerializeException.class,
                () -> localDateTimeCoercing.serialize(invalidType, context, locale));
    }

    @Test
    void localDateTimeScalar_parseValue_validString() {
        LocalDateTime expected = LocalDateTime.parse("2023-12-31T23:59", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertEquals(expected, localDateTimeScalar.getCoercing().parseValue("2023-12-31T23:59", context, locale));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023/12/31 23:59", "2023-12-31", "23:59-2023-12-31", "wrong"})
    void localDateTimeScalar_parseValue_invalidFormat(String input) {
        Coercing<?, ?> localDateTimeCoercing = localDateTimeScalar.getCoercing();
        CoercingParseValueException exception = assertThrows(CoercingParseValueException.class,
                () -> localDateTimeCoercing.parseValue(input, context, locale));
        assertEquals("Invalid LocalDateTime format. Expected '" + DateTimeFormatter.ISO_LOCAL_DATE_TIME +"'.", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void localDateTimeScalar_parseValue_invalidType() {
        assertThrows(CoercingParseValueException.class, () -> localDateTimeScalar.getCoercing().parseValue(123.45, context, locale));
    }

    @Test
    void localDateTimeScalar_parseLiteral_validStringValue() {
        StringValue input = new StringValue("2025-05-10T18:00");
        LocalDateTime expected = LocalDateTime.parse("2025-05-10T18:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertEquals(expected, localDateTimeScalar.getCoercing().parseLiteral(input, null, context, locale));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"2025/05/10 18:00", "2025-05-10", "18:00-2025-05-10", "bad"})
    void localDateTimeScalar_parseLiteral_invalidFormat(String input) {
        StringValue stringValue = new StringValue(input);
        Coercing<?, ?> localDateTimeCoercing = localDateTimeScalar.getCoercing();
        CoercingParseLiteralException exception = assertThrows(CoercingParseLiteralException.class,
                () -> localDateTimeCoercing.parseLiteral(stringValue, null, context, locale));
        assertEquals("Invalid LocalDateTime literal. Expected '" + DateTimeFormatter.ISO_LOCAL_DATE_TIME+"'.", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    void localDateTimeScalar_parseLiteral_invalidType() {

        graphql.language.IntValue input = new graphql.language.IntValue(BigInteger.valueOf(456));
        assertThrows(CoercingParseLiteralException.class, () -> localDateTimeScalar.getCoercing().parseLiteral(input, null, context, locale));
    }
}