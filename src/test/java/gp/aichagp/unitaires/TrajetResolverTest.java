package gp.aichagp.unitaires;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.models.TrajetInput;
import gp.aichagp.resolvers.TrajetResolver;
import gp.aichagp.services.TrajetService;
import gp.aichagp.services.GPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
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

    @InjectMocks
    private TrajetResolver trajetResolver;

    private Trajet trajet;
    private static final String TEST_DATE = "2024-03-29T10:00:00";

    @BeforeEach
    void setUp() {
        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(TEST_DATE);
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
        assertEquals("Lyon", result.get(0).getPointArrivee());
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
        assertEquals("Lyon", result.get(0).getPointArrivee());
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
        assertEquals("Lyon", result.get(0).getPointArrivee());
    }

    @Test
    void testSearchTrajetsProchesWithInvalidDate() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        String invalidDate = "2024-03-29"; // Format incorrect
        double rayonKm = 10.0;

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetResolver.searchTrajetsProches(pointDepart, pointArrivee, invalidDate, rayonKm);
        });
    }

    @Test
    void testCreateTrajet() {
        // Given
        TrajetInput input = new TrajetInput(
            "Paris",
            "Lyon",
            TEST_DATE,
            100.0,
            50.0
        );

        when(trajetService.createTrajet(any(Trajet.class))).thenReturn(trajet);

        // When
        Trajet result = trajetResolver.createTrajet(input);

        // Then
        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        assertEquals("Lyon", result.getPointArrivee());
    }

    @Test
    void testCreateTrajetWithInvalidDate() {
        // Given
        TrajetInput input = new TrajetInput(
            "Paris",
            "Lyon",
            "2024-03-29", // Format incorrect
            100.0,
            50.0
        );

        // When & Then
        assertThrows(TrajetValidationException.class, () -> {
            trajetResolver.createTrajet(input);
        });
    }
}