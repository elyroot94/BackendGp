package gp.aichagp.unitaires;

import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.dto.TrajetInput;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrajetResolverTest {

    @Mock
    private TrajetService trajetService;

    @Mock
    private GPService gpService;

    @InjectMocks
    private TrajetResolver trajetResolver;

    private Trajet trajet;
    private static final String GP_ID = "78968966";
    private static final LocalDateTime FUTURE_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime PAST_DATE = LocalDateTime.now().minusDays(1);

    @BeforeEach
    void setUp() {
        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setDateDepart(FUTURE_DATE);
        trajet.setCapaciteMaxKilos(100.0);
        trajet.setKilosDisponibles(50.0);
        trajet.setGpId(GP_ID);
    }

    @Test
    void getAllTrajets_shouldReturnTrajetsList() {
        when(trajetService.getAllTrajets()).thenReturn(List.of(trajet));

        List<Trajet> result = trajetResolver.getAllTrajets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Paris", result.getFirst().getPointDepart());
        verify(trajetService).getAllTrajets();
    }

    @Test
    void searchTrajets_shouldReturnFilteredTrajets() {
        when(trajetService.searchTrajets("Paris", "Lyon", FUTURE_DATE.toString()))
                .thenReturn(List.of(trajet));

        List<Trajet> result = trajetResolver.searchTrajets(
                "Paris", "Lyon", FUTURE_DATE.toString());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trajetService).searchTrajets("Paris", "Lyon", FUTURE_DATE.toString());
    }

    @Test
    void searchTrajetsProches_shouldReturnNearbyTrajets() {
        when(gpService.findTrajetsProches("Paris", "Lyon", FUTURE_DATE.toString(), 10.0))
                .thenReturn(List.of(trajet));

        List<Trajet> result = trajetResolver.searchTrajetsProches(
                "Paris", "Lyon", FUTURE_DATE.toString(), 10.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gpService).findTrajetsProches("Paris", "Lyon", FUTURE_DATE.toString(), 10.0);
    }

    @Test
    void trajet_shouldReturnTrajetById() {
        when(trajetService.getTrajetById("1")).thenReturn(trajet);

        Trajet result = trajetResolver.trajet("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(trajetService).getTrajetById("1");
    }

    @Test
    void createTrajet_shouldCreateNewTrajet() {
        TrajetInput input = new TrajetInput(
                "Paris", "Lyon", FUTURE_DATE, 100.0, 50.0, GP_ID);

        when(trajetService.createTrajet(any(Trajet.class))).thenReturn(trajet);

        Trajet result = trajetResolver.createTrajet(input);

        assertNotNull(result);
        assertEquals("Paris", result.getPointDepart());
        verify(trajetService).createTrajet(any(Trajet.class));
    }

    @Test
    void createTrajet_withNullInput_shouldThrowException() {
        assertThrows(TrajetValidationException.class,
                () -> trajetResolver.createTrajet(null));

        verify(trajetService, never()).createTrajet(any());
    }

    @Test
    void createTrajet_withPastDate_shouldThrowException() {
        TrajetInput input = new TrajetInput(
                "Paris", "Lyon", PAST_DATE, 100.0, 50.0, GP_ID);

        assertThrows(TrajetValidationException.class,
                () -> trajetResolver.createTrajet(input));

        verify(trajetService, never()).createTrajet(any());
    }
}