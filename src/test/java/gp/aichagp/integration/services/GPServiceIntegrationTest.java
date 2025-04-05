package gp.aichagp.integration.services;

import gp.aichagp.models.User;
import gp.aichagp.models.Trajet;
import gp.aichagp.services.GPService;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.repositories.TrajetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class GPServiceIntegrationTest {

    @Autowired
    private GPService gpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TEST_DATE = "2024-12-31T10:00:00";

    @BeforeEach
    public void setUp() {
        // Nettoyer la base de données avant chaque test
        mongoTemplate.remove(new Query(), User.class);
        mongoTemplate.remove(new Query(), Trajet.class);
    }

    @Test
    public void shouldFindGPProches() {
        // Given
        User gp1 = new User();
        gp1.setNom("GP1");
        gp1.setEmail("gp1@example.com");
        gp1.setAdresse("Tour Eiffel, Paris");
        gp1.setRole("GP");
        gp1.setLocation(new double[]{2.2945, 48.8584}); // Coordonnées de la Tour Eiffel
        userRepository.save(gp1);

        User gp2 = new User();
        gp2.setNom("GP2");
        gp2.setEmail("gp2@example.com");
        gp2.setAdresse("Notre-Dame, Paris");
        gp2.setRole("GP");
        gp2.setLocation(new double[]{2.3522, 48.8566}); // Coordonnées de Notre-Dame
        userRepository.save(gp2);

        // When
        List<User> gpsProches = gpService.findGPProches("Paris", 10);
        System.out.println(gpsProches);
        // Then
        assertNotNull(gpsProches);
        assertTrue(gpsProches.size() >= 2);
    }

    @Test
    public void shouldFindGPParTrajet() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(TEST_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        User gp = new User();
        gp.setNom("Test GP");
        gp.setEmail("test@example.com");
        gp.setAdresse("Tour Eiffel, Paris");
        gp.setRole("GP");
        gp.getTrajets().add(trajet);
        gp.setLocation(new double[]{2.2945, 48.8584}); // Coordonnées de la Tour Eiffel

        trajetRepository.save(trajet);
        User savedGP = userRepository.save(gp);

        // When
        List<User> gps = gpService.findGPParTrajet("Paris", "Lyon", 10);

        // Then
        assertNotNull(gps);
        assertFalse(gps.isEmpty());
        assertTrue(gps.stream().anyMatch(g -> g.getId().equals(savedGP.getId())));
    }
} 