package gp.aichagp.unitaires;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.services.TrajetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrajetServiceTest {

    @Mock
    private TrajetRepository trajetRepository;

    @InjectMocks
    private TrajetService trajetService;

    private Trajet trajet;
    private static final String TEST_DATE = "2025-12-31T10:00:00";

    @BeforeEach
    void setUp() {
        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(TEST_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(50.0);
    }

    @Test
    void testCreateTrajet() {
        // Given
        when(trajetRepository.save(any(Trajet.class))).thenReturn(trajet);

        // When
        Trajet result = trajetService.createTrajet(trajet);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        assertEquals("Lyon", result.getPointArrivee());
        verify(trajetRepository, times(1)).save(any(Trajet.class));
    }

    @Test
    void testCreateTrajetWithNullTrajet() {
        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(null);
        });
    }

    @Test
    void testCreateTrajetWithInvalidDate() {
        // Given
        trajet.setDateDepart("2024-03-29"); // Format incorrect

        // When & Then
        assertThrows(DateFormatException.class, () -> {
            trajetService.createTrajet(trajet);
        });
    }

    @Test
    void testCreateTrajetWithInvalidKilos() {
        // Given
        trajet.setKilosDisponibles(150.0); // Supérieur à la capacité max

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetService.createTrajet(trajet);
        });
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
        assertEquals("Paris", result.get(0).getPointDepart());
        verify(trajetRepository, times(1)).findAll();
    }

    @Test
    void testSearchTrajets() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        String dateDepart = TEST_DATE;

        when(trajetRepository.findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
            eq(pointDepart), eq(pointArrivee), any(String.class)))
            .thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetService.searchTrajets(pointDepart, pointArrivee, dateDepart);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).getPointDepart());
        verify(trajetRepository, times(1))
            .findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
                eq(pointDepart), eq(pointArrivee), any(String.class));
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
        verify(trajetRepository, times(1)).findById(id);
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
    }
} 