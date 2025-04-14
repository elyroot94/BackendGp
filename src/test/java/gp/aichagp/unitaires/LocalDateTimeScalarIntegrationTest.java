package gp.aichagp.unitaires;

import gp.aichagp.config.LocalDateScalar;
import gp.aichagp.config.LocalDateTimeScalar;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeScalarIntegrationTest {

    @Test
    void shouldHandleLocalDateTimeThroughGraphQL() {
        // Arrange
        String schema = "scalar LocalDateTime\n" +
                "type Query { testDateTime: LocalDateTime }\n" +
                "type Mutation { echoDateTime(input: LocalDateTime!): LocalDateTime }";

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .scalar(LocalDateTimeScalar.createLocalDateTimeScalar())
                .type("Query", builder -> builder.dataFetcher("testDateTime", env -> LocalDateTime.now()))
                .type("Mutation", builder -> builder.dataFetcher("echoDateTime", env -> env.getArgument("input")))
                .build();

        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(
                new SchemaParser().parse(schema),
                wiring);

        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        String testDateTimeStr = "2023-12-31T10:30:00";

        // Act - Test Query
        ExecutionResult queryResult = graphQL.execute(ExecutionInput.newExecutionInput()
                .query("{ testDateTime }")
                .build());

        // Assert - Query
        assertTrue(queryResult.getErrors().isEmpty(),
                "Should not have errors but got: " + queryResult.getErrors());

        // Act - Test Mutation
        ExecutionResult mutationResult = graphQL.execute(ExecutionInput.newExecutionInput()
                .query("mutation($input: LocalDateTime!) { echoDateTime(input: $input) }")
                .variables(Map.of("input", testDateTimeStr))
                .build());

        // Assert - Mutation
        assertTrue(mutationResult.getErrors().isEmpty(),
                "Should not have errors but got: " + mutationResult.getErrors());

        String result = mutationResult.getData().toString();
        assertTrue(result.contains(testDateTimeStr),
                "Expected date in response but got: " + result);
    }

    @Test
    void shouldHandleLocalDateThroughGraphQL() {
        // Arrange
        String schema = "scalar LocalDate\n" +
                "type Query { testDate: LocalDate }\n" +
                "type Mutation { echoDate(input: LocalDate!): LocalDate }";

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .scalar(LocalDateScalar.createLocalDateScalar())
                .type("Query", builder -> builder.dataFetcher("testDate", env -> LocalDate.now()))
                .type("Mutation", builder -> builder.dataFetcher("echoDate", env -> env.getArgument("input")))
                .build();

        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(
                new SchemaParser().parse(schema),
                wiring);

        GraphQL graphQL = GraphQL.newGraphQL(graphQLSchema).build();

        String testDateStr = "2023-12-31";

        // Act - Test Query
        ExecutionResult queryResult = graphQL.execute(ExecutionInput.newExecutionInput()
                .query("{ testDate }")
                .build());

        // Assert - Query
        assertTrue(queryResult.getErrors().isEmpty(),
                "Should not have errors but got: " + queryResult.getErrors());

        // Act - Test Mutation
        ExecutionResult mutationResult = graphQL.execute(ExecutionInput.newExecutionInput()
                .query("mutation($input: LocalDate!) { echoDate(input: $input) }")
                .variables(Map.of("input", testDateStr))
                .build());

        // Assert - Mutation
        assertTrue(mutationResult.getErrors().isEmpty(),
                "Should not have errors but got: " + mutationResult.getErrors());

        String result = mutationResult.getData().toString();
        assertTrue(result.contains(testDateStr),
                "Expected date in response but got: " + result);
    }



}