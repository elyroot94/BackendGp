package gp.aichagp.integration.resolvers;

import gp.aichagp.AichagpApplication;
import gp.aichagp.models.User;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.services.GPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(classes = {AichagpApplication.class})
@ActiveProfiles("test")
public class GPResolverTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.2")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;
    private HttpGraphQlTester graphQlTester;

    @MockBean
    private GPService gpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Attendre que le conteneur soit prêt
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start();
        }
        
        // Nettoyer la base de données avant chaque test
        mongoTemplate.getDb().drop();
        
        // Initialiser le WebTestClient avec la configuration CORS
        this.webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port + "/graphql")
            .defaultHeader("Origin", "http://localhost:" + port)
            .defaultHeader("Access-Control-Request-Method", "POST")
            .build();
        
        // Configurer le HttpGraphQlTester avec le WebTestClient
        this.graphQlTester = HttpGraphQlTester.create(webTestClient);
    }

    @Test
    void testSearchGPProches() {
        // Given
        String ville = "Paris";
        double rayonKm = 10.0;
        
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

        when(gpService.findGPProches(eq(ville), eq(rayonKm)))
                .thenReturn(Arrays.asList(gp1, gp2));

        // When
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
        graphQlTester.document(query)
                .execute()
                .path("searchGPProches")
                .entityList(User.class)
                .hasSize(2)
                .contains(gp1, gp2);
    }

    @Test
    void testSearchGPParTrajet() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        double rayonKm = 10.0;

        User gp = new User();
        gp.setNom("Test GP");
        gp.setEmail("test@example.com");
        gp.setAdresse("Tour Eiffel, Paris");
        gp.setRole("GP");
        gp.setLocation(new double[]{2.2945, 48.8584});

        when(gpService.findGPParTrajet(eq(pointDepart), eq(pointArrivee), eq(rayonKm)))
                .thenReturn(Arrays.asList(gp));

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

        // Then
        graphQlTester.document(query)
                .execute()
                .path("searchGPParTrajet")
                .entityList(User.class)
                .hasSize(1)
                .contains(gp);
    }

    @Test
    void testGetGPById() {
        // Given
        String id = "123";
        User gp = new User();
        gp.setId(id);
        gp.setNom("Test GP");
        gp.setEmail("test@example.com");
        gp.setRole("GP");

        when(gpService.getGPById(eq(id))).thenReturn(gp);

        // When
        String query = """
        {
          getGPById(id: "123") {
            id
            nom
            email
            role
          }
        }
        """;

        // Then
        graphQlTester.document(query)
                .execute()
                .path("getGPById")
                .entity(User.class)
                .isEqualTo(gp);
    }

    @Test
    void testRegisterGP() {
        // Given
        User gp = new User();
        gp.setNom("Nouveau GP");
        gp.setPrenom("Test");
        gp.setEmail("nouveau@example.com");
        gp.setTelephone("0123456789");
        gp.setPassword("password");
        gp.setRole("GP");
        gp.setAdresse("Paris");

        when(gpService.registerGP(any(User.class))).thenReturn(gp);

        // When
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
            nom
            prenom
            email
            telephone
            role
            adresse
          }
        }
        """;

        // Then
        graphQlTester.document(mutation)
                .execute()
                .path("registerGP")
                .entity(User.class)
                .satisfies(registeredGP -> {
                    assert registeredGP.getNom().equals("Nouveau GP");
                    assert registeredGP.getPrenom().equals("Test");
                    assert registeredGP.getEmail().equals("nouveau@example.com");
                    assert registeredGP.getRole().equals("GP");
                });
    }
} 