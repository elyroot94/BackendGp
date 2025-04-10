package gp.aichagp.integration.resolvers;

import gp.aichagp.TestIntroGraphqlApplication;
import gp.aichagp.models.Trajet;
import gp.aichagp.models.User;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {TestIntroGraphqlApplication.class})
@ActiveProfiles("test")
@Slf4j
public class TrajetResolverTest {


    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TrajetResolverTest.class);


    @Autowired
    TrajetRepository trajetRepository;
    @Autowired
    HttpGraphQlTester httpGraphQlTester;


    @Autowired
    UserRepository userRepository;

    Trajet createdTrajet;




    @Autowired
    private MongoTemplate mongoTemplate;

    private Trajet trajet;
    private static final String TEST_DATE = "2024-03-29T10:00:00";

    @BeforeEach
    void setUp() throws InterruptedException {

    }


    /**
     * Tests the creation of a new Trajet.
     * This test ensures that a Trajet can be created successfully via a GraphQL mutation
     * and verifies the correctness of the returned Trajet details.
     */
    @Test
    @Order(1)
    void testCreateTrajet() {
        // Clear existing Trajets from the database to ensure a clean state
        this.trajetRepository.deleteAll();

        // Create a new GP (General Practitioner) user
        User gp = new User();
        gp.setNom("Jean");
        gp.setPrenom("GP");
        gp.setEmail("gp@example.com");
        gp.setAdresse("Paris");
        gp.setRole("GP");

        // Save the GP to the database
        User savedGp = userRepository.save(gp);

        // Log the ID of the created GP for debugging purposes
        logger.info("GP créé avec ID: {}", savedGp.getId());

        // Prepare the GraphQL mutation query to create a new Trajet
        String query = String.format(
            """
            mutation {
              createTrajet(
                input: {pointDepart: "Paris", pointArrivee: "Nouakchott", dateDepart: "2025-12-31T10:00:00", capaciteMaxKilos: 10, kilosDisponibles: 7, gpId: "%s" }
              ) {
                id
                pointDepart
                pointArrivee
                dateDepart
              }
            }
            """, savedGp.getId());

        // Execute the GraphQL mutation and capture the created Trajet
        Trajet savedTrajet = this.httpGraphQlTester.document(query)
            .execute()
            .errors()
            .verify()
            .path("createTrajet")
            .entity(Trajet.class)
            .get();

        // Verify that the created Trajet has a non-null ID and matches the expected details
        assertThat(savedTrajet.getId()).isNotNull();
        assertThat(savedTrajet.getPointDepart()).isEqualTo("Paris");
        assertThat(savedTrajet.getPointArrivee()).isEqualTo("Nouakchott");
        assertThat(savedTrajet.getDateDepart()).isEqualTo("2025-12-31T10:00:00");

        // Store the created Trajet for potential use in other tests
        createdTrajet = savedTrajet;
    }


    /**
     * Tests the getAllTrajets GraphQL query for retrieving all Trajets.
     * It verifies that the query returns the expected list of Trajet details.
     */
    @Test
    @Order(2)
    void testGetAllTrajets() {
        // Define the GraphQL query for retrieving all Trajets
        String query = """
                {
                  getAllTrajets {
                    id
                    pointDepart
                    pointArrivee
                    dateDepart
                    capaciteMaxKilos
                    kilosDisponibles
                  }
                }
                """;

        // Execute the GraphQL query
        List<Trajet> trajets = this.httpGraphQlTester.document(query)
                .execute()
                .errors()
                .verify()
                .path("getAllTrajets")
                .entityList(Trajet.class)
                .get();

        // Verify the result
        assertThat(trajets.size()).isEqualTo(1);
        Trajet t = trajets.getFirst();
        assertThat(t.getPointDepart()).isEqualTo("Paris");
        assertThat(t.getPointArrivee()).isEqualTo("Nouakchott");
        assertThat(t.getDateDepart()).isEqualTo("2025-12-31T10:00:00");
        assertThat(t.getCapaciteMaxKilos()).isEqualTo(10.0);
    }

    /**
     * Test the searchTrajets GraphQL query for retrieving trajets.
     * It verifies that the query returns the expected trajet details.
     */
    @Test
    @Order(3)
    void testSearchTrajets() {
        // Define the GraphQL query for searching trajets
        String query = """
                {
                  searchTrajets(pointDepart: "Paris", pointArrivee: "Nouakchott", dateDepart: "2025-12-31T10:00:00") {
                    id
                    pointDepart
                    pointArrivee
                    dateDepart
                    capaciteMaxKilos
                    kilosDisponibles
                  }
                }
                """;

        // Execute the query and retrieve the list of trajets
        List<Trajet> trajets = this.httpGraphQlTester.document(query)
                .execute()
                .errors()
                .verify()
                .path("searchTrajets")
                .entityList(Trajet.class)
                .get();

        // Verify that one trajet is returned
        assertThat(trajets.size()).isEqualTo(1);

        // Retrieve the first trajet and verify its details
        Trajet t = trajets.getFirst();
        assertThat(t.getPointDepart()).isEqualTo("Paris");
        assertThat(t.getPointArrivee()).isEqualTo("Nouakchott");
        assertThat(t.getDateDepart()).isEqualTo("2025-12-31T10:00:00");
        assertThat(t.getCapaciteMaxKilos()).isEqualTo(10.0);
    }


}
