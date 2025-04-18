package gp.aichagp.unitaires;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrajetServiceTest {

    @Mock
    private TrajetRepository trajetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrajetService trajetService;

    private Trajet trajet;
    private static final String TEST_DATE = "2025-12-31T10:00:00";
    private static final String GP_ID = "gp123";

    @BeforeEach
    void setUp() {
        trajet = new Trajet();
        trajet.setId("1");
        trajet.setGpId(GP_ID);
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(LocalDateTime.parse(TEST_DATE));
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(50.0);
    }

    @Test
    void testCreateTrajet_Success() {
        // Given
        when(userRepository.existsById(GP_ID)).thenReturn(true);
        when(trajetRepository.save(any(Trajet.class))).thenReturn(trajet);

        // When
        Trajet result = trajetService.createTrajet(trajet);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        assertEquals("Lyon", result.getPointArrivee());
        verify(userRepository).existsById(GP_ID);
        verify(trajetRepository).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_GPNotFound() {
        // Given
        when(userRepository.existsById(GP_ID)).thenReturn(false);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        }, "GP non trouvé: " + GP_ID);
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajetWithNullTrajet() {
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(null);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_EmptyPointDepart() {
        // Given
        trajet.setPointDepart("");

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_EmptyPointArrivee() {
        // Given
        trajet.setPointArrivee("");

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NegativeCapaciteMax() {
        // Given
        trajet.setCapaciteMaxKilos(-10.0);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NegativeKilosDisponibles() {
        // Given
        trajet.setKilosDisponibles(-5.0);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajetWithInvalidKilos() {
        // Given
        trajet.setKilosDisponibles(150.0);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NullPointDepart() {
        // Given
        trajet.setPointDepart(null);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NullPointArrivee() {
        // Given
        trajet.setPointArrivee(null);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NullCapaciteMax() {
        // Given
        trajet.setCapaciteMaxKilos(null);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NullKilosDisponibles() {
        // Given
        trajet.setKilosDisponibles(null);

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
        verify(trajetRepository, never()).save(any(Trajet.class));
    }

    @Test
    void testGetAllTrajets() {
        // Given
        when(trajetRepository.findAll()).thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetService.getAllTrajets();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.getFirst().getPointDepart());
        verify(trajetRepository).findAll();
    }

    @Test
    void testSearchTrajets() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";

        when(trajetRepository.findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
            eq(pointDepart), eq(pointArrivee), any(LocalDateTime.class)))
            .thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetService.searchTrajets(pointDepart, pointArrivee, TEST_DATE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.getFirst().getPointDepart());
        verify(trajetRepository).findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
            eq(pointDepart), eq(pointArrivee), any(LocalDateTime.class));
    }

    @Test
    void testGetTrajetById() {
        // Given
        String id = "1";
        when(trajetRepository.findById(id)).thenReturn(Optional.of(trajet));

        // When
        Trajet result = trajetService.getTrajetById(id);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        verify(trajetRepository).findById(id);
    }

    @Test
    void testGetTrajetByIdNotFound() {
        // Given
        String id = "999";
        when(trajetRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.getTrajetById(id);
        });
        verify(trajetRepository).findById(id);
    }

    @Test
    void testGetTrajetById_NotFound() {
        // Given
        String id = "nonexistent";
        when(trajetRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.getTrajetById(id);
        }, "Trajet non trouvé avec l'ID: " + id);
    }

    @Test
    void testSearchTrajets_InvalidDateFormat() {
        // Given
        String invalidDate = "invalid-date";

        // When & Then
        DateFormatException exception = assertThrows(DateFormatException.class, () -> {
            trajetService.searchTrajets("Paris", "Lyon", invalidDate);
        });
        
        assertEquals("Format de date invalide. Utilisez le format ISO-8601 (ex: 2025-12-31T10:00:00)", 
                    exception.getMessage());
        
        verify(trajetRepository, never())
            .findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(any(), any(), any());
    }

    @Test
    void testSearchTrajets_NullParams() {
        // Test null pointDepart
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.searchTrajets(null, "Lyon", TEST_DATE);
        });

        // Test null pointArrivee
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.searchTrajets("Paris", null, TEST_DATE);
        });

        // Test null dateDepart
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.searchTrajets("Paris", "Lyon", null);
        });

        verify(trajetRepository, never())
            .findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(any(), any(), any());
    }

    @Test
    void testSearchTrajets_EmptyParams() {
        // Test empty pointDepart
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.searchTrajets("", "Lyon", TEST_DATE);
        });

        // Test empty pointArrivee
        assertThrows(IllegalArgumentException.class, () -> {
            trajetService.searchTrajets("Paris", "", TEST_DATE);
        });

        verify(trajetRepository, never())
            .findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(any(), any(), any());
    }
}