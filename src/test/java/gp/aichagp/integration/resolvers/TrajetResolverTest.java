package gp.aichagp.integration.resolvers;

import gp.aichagp.AichagpApplication;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.resolvers.TrajetResolver;
import gp.aichagp.services.GPService;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@ContextConfiguration(classes = {AichagpApplication.class})
@ActiveProfiles("test")
public class TrajetResolverTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @MockBean
    private TrajetRepository trajetRepository;

    @MockBean
    private TrajetService trajetService;

    @MockBean
    private GPService gpService;

    private Trajet trajet;
    private static final String TEST_DATE = "2024-03-29T10:00:00";

    @BeforeEach
    void setUp() {
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
