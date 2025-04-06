package gp.aichagp.integration.resolvers;

import gp.aichagp.AichagpApplication;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.resolvers.TrajetResolver;
import gp.aichagp.services.GPService;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class TrajetResolverTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.2")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> 
            String.format("mongodb://%s:%d/test", 
                mongoDBContainer.getHost(), 
                mongoDBContainer.getFirstMappedPort()));
    }

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;
    private HttpGraphQlTester graphQlTester;

    @MockBean
    private TrajetRepository trajetRepository;

    @MockBean
    private TrajetService trajetService;

    @MockBean
    private GPService gpService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Trajet trajet;
    private static final String TEST_DATE = "2024-03-29T10:00:00";

    @BeforeEach
    void setUp() throws InterruptedException {
        // Attendre que le conteneur soit prêt
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start();
        }
        
        // Attendre 5 secondes pour que le conteneur soit complètement initialisé
        Thread.sleep(5000);
        
        // Nettoyer la base de données avant chaque test
        mongoTemplate.getDb().drop();
        mongoTemplate.createCollection(Trajet.class);

        // Créer l'index géospatial
        mongoTemplate.indexOps(Trajet.class).ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));

        // Initialiser le WebTestClient avec la configuration CORS
        this.webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port + "/graphql")
            .defaultHeader("Origin", "http://localhost:" + port)
            .defaultHeader("Access-Control-Request-Method", "POST")
            .build();
        
        // Configurer le HttpGraphQlTester avec le WebTestClient
        this.graphQlTester = HttpGraphQlTester.create(webTestClient);

        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(TEST_DATE);

        when(trajetService.getAllTrajets()).thenReturn(List.of(trajet));
        when(trajetService.searchTrajets(eq("Paris"), eq("Lyon"), eq(TEST_DATE)))
            .thenReturn(List.of(trajet));
        when(gpService.findTrajetsProches(eq("Paris"), eq("Lyon"), eq(TEST_DATE), eq(10.0)))
            .thenReturn(List.of(trajet));
    }

    @Test
    void testGetAllTrajets() {
        String query = """
        {
          getAllTrajets {
            id
            pointDepart
            pointArrivee
          }
        }
        """;

        graphQlTester.document(query)
                .execute()
                .path("getAllTrajets[0].pointDepart")
                .entity(String.class)
                .isEqualTo("Paris");
    }

    @Test
    void testSearchTrajets() {
        String query = """
        {
          searchTrajets(pointDepart: "Paris", pointArrivee: "Lyon", dateDepart: "2024-03-29T10:00:00") {
            id
            pointDepart
            pointArrivee
          }
        }
        """;

        graphQlTester.document(query)
                .execute()
                .path("searchTrajets[0].pointArrivee")
                .entity(String.class)
                .isEqualTo("Lyon");
    }

    @Test
    void testSearchTrajetsProches() {
        String query = """
        {
          searchTrajetsProches(pointDepart: "Paris", pointArrivee: "Lyon", dateDepart: "2024-03-29T10:00:00", rayonKm: 10.0) {
            id
            pointDepart
            pointArrivee
          }
        }
        """;

        graphQlTester.document(query)
                .execute()
                .path("searchTrajetsProches[0].pointArrivee")
                .entity(String.class)
                .isEqualTo("Lyon");
    }
}
