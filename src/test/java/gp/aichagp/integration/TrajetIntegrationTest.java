package gp.aichagp.integration;

import gp.aichagp.models.Trajet;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class TrajetIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TrajetService trajetService;

    @Test
    void shouldCreateTrajetWithValidData() {
        // Arrange
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(LocalDateTime.now().plusDays(1));
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // Act
        Trajet savedTrajet = trajetService.createTrajet(trajet);

        // Assert
        assertThat(savedTrajet).isNotNull();
        assertThat(savedTrajet.getId()).isNotNull();
        assertThat(savedTrajet.getPointDepart()).isEqualTo("Paris");
        assertThat(savedTrajet.getPointArrivee()).isEqualTo("Lyon");
        assertThat(savedTrajet.getCapaciteMaxKilos()).isEqualTo(100.0);
        assertThat(savedTrajet.getKilosDisponibles()).isEqualTo(100.0);
    }

    @Test
    void shouldNotCreateTrajetWithPastDate() {
        // Arrange
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(LocalDateTime.now().minusDays(1));
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(100.0);

        // Act & Assert
        assertThatThrownBy(() -> trajetService.createTrajet(trajet))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("La date de départ ne peut pas être dans le passé");
    }

    @Test
    void shouldNotCreateTrajetWithNegativeKilos() {
        // Arrange
        Trajet trajet = new Trajet();
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(LocalDateTime.now().plusDays(1));
        trajet.setCapaciteMaxKilos(-100.0);
        trajet.setKilosDisponibles(-100.0);

        // Act & Assert
        assertThatThrownBy(() -> trajetService.createTrajet(trajet))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("La capacité et les kilos disponibles doivent être positifs");
    }
} 