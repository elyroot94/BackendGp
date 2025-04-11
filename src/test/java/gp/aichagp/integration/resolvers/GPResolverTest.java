package gp.aichagp.integration.resolvers;
import gp.aichagp.TestIntroGraphqlApplication;
import gp.aichagp.models.Trajet;
import gp.aichagp.models.User;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {TestIntroGraphqlApplication.class})
@ActiveProfiles("test")
class GPResolverTest {


    @Autowired
   private UserRepository userRepository;

    @Autowired
   private   TrajetRepository trajetRepository;



    @Autowired
  private   HttpGraphQlTester httpGraphQlTester;

    User saveUser;

    // Déclaration du record ici, local au test
    record GPInfo(String nom, String email, String adresse, String role) {}


    /**
     * Tests the searchGPProches method of the GPResolver
     */
    @Test
    @Order(4)
    void testSearchGPProches() {
        // Clean DB
        this.userRepository.deleteAll();
        // Given


        // Create two GPs with different locations
        User gp1 = new User();
        gp1.setNom("GP1");
        gp1.setEmail("gp1@example.com");
        gp1.setAdresse("Tour Eiffel, Paris");
        gp1.setRole("GP");
        gp1.setLocation(new double[]{2.2945, 48.8584});

        User gp2 = new User();
        gp2.setNom("GP2");
        gp2.setEmail("gp2@example.com");
        gp2.setAdresse("Notre-Dame, Paris");
        gp2.setRole("GP");
        gp2.setLocation(new double[]{2.3522, 48.8566});

        // Save the GPs
        this.userRepository.saveAll(Arrays.asList(gp1, gp2));

        // Query
        String query = """
        {
          searchGPProches(ville: "Paris", rayonKm: 10.0) {
            nom
            email
            adresse
            role
          }
        }
        """;

        // Then
        List<GPInfo> users= this.httpGraphQlTester.document(query)
                .execute()
                .errors()
                .verify()
                .path("searchGPProches")
                .entityList(GPInfo.class)
                .hasSize(2)
                .get();

        // Assertions
        assertThat(users).containsExactlyInAnyOrder(
                new GPInfo("GP1", "gp1@example.com", "Tour Eiffel, Paris", "GP"),
                new GPInfo("GP2", "gp2@example.com", "Notre-Dame, Paris", "GP")
        );
    }

        /**
         * Tests the searchGPParTrajet method of the GPResolver
         */
        @Test
        @Order(3)
        void testSearchGPParTrajet() {
            // Clean DB
            this.userRepository.deleteAll();
            // Given
            String pointDepart = "Paris";
            String pointArrivee = "Lyon";


            // Création du trajet avec format de date ISO (RFC 3339 / ISO 8601)

            User gp = new User();
            gp.setNom("Test GP");
            gp.setEmail("test@example.com");
            gp.setAdresse("Tour Eiffel, Paris");
            gp.setRole("GP");
            gp.setLocation(new double[]{2.2945, 48.8584});
            User savedGP = this.userRepository.save(gp);

            Trajet trajet = new Trajet();
            trajet.setPointDepart(pointDepart);
            trajet.setPointArrivee(pointArrivee);
            trajet.setDateDepart(LocalDateTime.parse("2025-04-10T10:00:00"));
            trajet.setDateArriveeEstimee(LocalDateTime.parse("2025-04-10T15:00:00"));
            trajet.setCapaciteMaxKilos(20.0);
            trajet.setKilosDisponibles(15.0);
            trajet.setGpId(savedGP.getId());
            trajetRepository.save(trajet);

            // When
            String query = """
            {
              searchGPParTrajet(pointDepart: "Paris", pointArrivee: "Lyon", rayonKm: 10.0) {
                nom
                email
                adresse
                role
              }
            }
            """;
            record UserDto(String nom, String email, String adresse, String role) {}

            List<UserDto> users = this.httpGraphQlTester.document(query)
                    .execute()
                    .errors()
                    .verify()
                    .path("searchGPParTrajet")
                    .entityList(UserDto.class)
                    .hasSize(1)
                    .get();

            assertThat(users).isNotEmpty();
            assertThat(users.getFirst().email()).isEqualTo("test@example.com");
        }

    /**
     * Tests the getGPById GraphQL query.
     * This test ensures that the correct GP details are retrieved by ID.
     */
    @Test
    @Order(2)
    void testGetGPById() {
        // Prepare the GraphQL query to retrieve a GP by ID
        String query = String.format("""
        query {
          getGPById(id: "%s") {
            id
            nom
            prenom
            email
            role
            adresse
          }
        }
        """, saveUser.getId());

        // Execute the query and capture the result
        User user = this.httpGraphQlTester.document(query)
                .execute()
                .errors() // Verify that there are no errors
                .verify()
                .path("getGPById") // Path to the expected entity in the response
                .entity(User.class) // Map the response to a User object
                .get();

        // Assert that the retrieved GP's details match the expected values
        assertThat(user.getNom()).isEqualTo("Nouveau GP");
        assertThat(user.getPrenom()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("nouveau@example.com");
        assertThat(user.getRole()).isEqualTo("GP");
        assertThat(user.getAdresse()).isEqualTo("Paris");
    }


    /**
     * Tests the registerGP GraphQL mutation.
     * This test ensures that a GP can be registered successfully and that the correct details are returned.
     */
    @Test
    @Order(1)
    void testRegisterGP() {
        // Prepare the GraphQL mutation query to register a GP
        String mutation = """
        mutation {
          registerGP(input: {
            nom: "Nouveau GP"
            prenom: "Test"
            email: "nouveau@example.com"
            telephone: "0123456789"
            password: "password"
            role: "GP"
            adresse: "Paris"
          }) {
            id
            nom
            prenom
            email
            telephone
            role
            adresse
          }
        }
        """;

        // Execute the mutation and capture the result
        User gp = this.httpGraphQlTester.document(mutation)
                .execute()
                .errors() // Verify that there are no errors
                .verify()
                .path("registerGP") // Path to the expected entity in the response
                .entity(User.class) // Map the response to a User object
                .get();

        // Assert that the registered GP's details match the expected values
        assertThat(gp.getId()).isNotNull();
        assertThat(gp.getNom()).isEqualTo("Nouveau GP");
        assertThat(gp.getPrenom()).isEqualTo("Test");
        assertThat(gp.getEmail()).isEqualTo("nouveau@example.com");
        assertThat(gp.getRole()).isEqualTo("GP");
        assertThat(gp.getAdresse()).isEqualTo("Paris");

        // Store the registered GP for later use
        saveUser=gp;
    }
} 