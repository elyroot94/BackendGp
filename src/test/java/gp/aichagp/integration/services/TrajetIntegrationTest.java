package gp.aichagp.integration.services;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TrajetIntegrationTest {

    @Autowired
    private TrajetService trajetService;

    @Autowired
    private TrajetRepository trajetRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String FUTURE_DATE = "2025-12-31T10:00:00";
    private static final String PAST_DATE = "2023-01-01T10:00:00";

    @BeforeEach
    void setUp() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void shouldCreateTrajetWithValidData() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(FUTURE_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When
        Trajet savedTrajet = trajetService.createTrajet(trajet);

        // Then
        assertNotNull(savedTrajet.getId());
        assertEquals("Paris", savedTrajet.getPointDepart());
        assertEquals("Lyon", savedTrajet.getPointArrivee());
        assertEquals(100.0, savedTrajet.getCapaciteMaxKilos());
        assertEquals(100.0, savedTrajet.getKilosDisponibles());
    }

    @Test
    void shouldNotCreateTrajetWithPastDate() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(PAST_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        TrajetValidationException exception = assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        assertEquals("La date de départ ne peut pas être dans le passé", exception.getMessage());
    }

    @Test
    void shouldNotCreateTrajetWithNegativeKilos() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(FUTURE_DATE);
        trajet.setCapaciteMaxKilos(-100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        TrajetValidationException exception = assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        assertEquals("La capacité maximale doit être supérieure à 0", exception.getMessage());
    }

    @Test
    void shouldNotCreateTrajetWithInvalidDateFormat() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart("2024/03/29 10:00"); // Format incorrect
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        DateFormatException exception = assertThrows(DateFormatException.class, () -> {
            trajetService.createTrajet(trajet);
        });

        assertEquals(
                "Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)",
                exception.getMessage()
        );
    }

    @Test
    void shouldNotCreateTrajetWithNullDate() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(null);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        TrajetValidationException exception = assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        assertEquals("La date de départ est obligatoire", exception.getMessage());
    }

    @Test
    void shouldNotCreateTrajetWithEmptyPointDepart() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(FUTURE_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        TrajetValidationException exception = assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        assertEquals("Le point de départ est obligatoire", exception.getMessage());
    }

    @Test
    void shouldNotCreateTrajetWithKilosDisponiblesGreaterThanCapaciteMax() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(FUTURE_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(150.0);

        // When/Then
        TrajetValidationException exception = assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        assertEquals("Les kilos disponibles ne peuvent pas dépasser la capacité maximale", exception.getMessage());
    }

    @Test
    void shouldNotCreateTrajetWithMalformedDate() {
        // Given
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart("29-03-2024 10:00"); // Format incorrect
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // When/Then
        DateFormatException exception = assertThrows(DateFormatException.class, () -> {
            trajetService.createTrajet(trajet);
        });

        assertEquals(
                "Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)",
                exception.getMessage()
        );
    }
} 