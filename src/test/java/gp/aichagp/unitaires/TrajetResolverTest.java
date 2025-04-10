package gp.aichagp.unitaires;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.models.TrajetInput;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.resolvers.TrajetResolver;
import gp.aichagp.services.TrajetService;
import gp.aichagp.services.GPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrajetResolverTest {

    @Mock
    private TrajetService trajetService;

    @Mock
    private GPService gpService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrajetResolver trajetResolver;

    private Trajet trajet;
    private static final String TEST_DATE = "2025-12-31T10:00:00";
    private static final String GP_ID = "78968966";

    @BeforeEach
    void setUp() {
        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(LocalDateTime.parse(TEST_DATE));
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(50.0);
        trajet.setGpId(GP_ID);
    }

    @Test
    void testGetAllTrajets() {
        // Given
        when(trajetService.getAllTrajets()).thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetResolver.getAllTrajets();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).getPointDepart());
        verify(trajetService).getAllTrajets();
    }

    @Test
    void testSearchTrajets() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        String dateDepart = TEST_DATE;

        when(trajetService.searchTrajets(eq(pointDepart), eq(pointArrivee), eq(dateDepart)))
            .thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetResolver.searchTrajets(pointDepart, pointArrivee, dateDepart);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).getPointDepart());
        verify(trajetService).searchTrajets(pointDepart, pointArrivee, dateDepart);
    }

    @Test
    void testSearchTrajetsWithInvalidDate() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        String invalidDate = "2024-03-29"; // Format incorrect

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetResolver.searchTrajets(pointDepart, pointArrivee, invalidDate);
        });
        verify(trajetService, never()).searchTrajets(any(), any(), any());
    }

    @Test
    void testSearchTrajetsProches() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        String dateDepart = TEST_DATE;
        double rayonKm = 10.0;

        when(gpService.findTrajetsProches(eq(pointDepart), eq(pointArrivee), eq(dateDepart), eq(rayonKm)))
            .thenReturn(List.of(trajet));

        // When
        List<Trajet> result = trajetResolver.searchTrajetsProches(pointDepart, pointArrivee, dateDepart, rayonKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.get(0).getPointDepart());
        verify(gpService).findTrajetsProches(pointDepart, pointArrivee, dateDepart, rayonKm);
    }

    @Test
    void testTrajetById() {
        // Given
        String id = "1";
        when(trajetService.getTrajetById(id)).thenReturn(trajet);

        // When
        Trajet result = trajetResolver.trajet(id);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        verify(trajetService).getTrajetById(id);
    }

    @Test
    void testCreateTrajet_Success() {
        // Given
        TrajetInput input = new TrajetInput(
            "Paris",
            "Lyon",
            TEST_DATE,
            100.0,
            50.0,
            GP_ID
        );

        when(trajetService.createTrajet(any(Trajet.class))).thenReturn(trajet);

        // When
        Trajet result = trajetResolver.createTrajet(input);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        assertEquals("Lyon", result.getPointArrivee());
        verify(trajetService).createTrajet(any(Trajet.class));
    }

    @Test
    void testCreateTrajet_NullInput() {
        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetResolver.createTrajet(null);
        });
        verify(trajetService, never()).createTrajet(any());
    }

    @Test
    void testCreateTrajet_InvalidDateFormat() {
        // Given
        TrajetInput input = new TrajetInput(
            "Paris",
            "Lyon",
            "2024-03-29", // Format incorrect
            100.0,
            50.0,
            GP_ID
        );

        // When & Then
        assertThrows(DateFormatException.class, () -> {
            trajetResolver.createTrajet(input);
        });
        verify(trajetService, never()).createTrajet(any());
    }

    @Test
    void testCreateTrajet_PastDate() {
        // Given
        TrajetInput input = new TrajetInput(
            "Paris",
            "Lyon",
            "2020-03-29T10:00:00", // Date passée
            100.0,
            50.0,
            GP_ID
        );

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetResolver.createTrajet(input);
        });
        verify(trajetService, never()).createTrajet(any());
    }
}