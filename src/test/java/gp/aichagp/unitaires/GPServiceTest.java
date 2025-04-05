package gp.aichagp.unitaires;

import gp.aichagp.models.User;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.services.GPService;
import gp.aichagp.services.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GPServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private GPService gpService;

    private User gp;
    private Trajet trajet;

    @BeforeEach
    void setUp() {
        gp = new User();
        gp.setId("1");
        gp.setNom("John");
        gp.setPrenom("Doe");
        gp.setEmail("john.doe@example.com");
        gp.setTelephone("0123456789");
        gp.setPassword("password");
        gp.setRole("GP");
        gp.setAdresse("Paris");
        gp.setLocation(new double[]{2.3522, 48.8566});

        trajet = new Trajet();
        trajet.setId("1");
        trajet.setPointDepart("Paris");
        trajet.setPointArrivee("Lyon");
        trajet.setGp(gp);

        List<Trajet> trajets = new ArrayList<>();
        trajets.add(trajet);
        gp.setTrajets(trajets);
    }

    @Test
    void testRegisterGP() {
        // Given
        when(geocodingService.geocodeAddress(anyString())).thenReturn(new double[]{2.3522, 48.8566});
        when(userRepository.save(any(User.class))).thenReturn(gp);

        // When
        User result = gpService.registerGP(gp);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getNom());
        assertEquals("GP", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
        verify(geocodingService, times(1)).geocodeAddress(anyString());
    }

    @Test
    void testRegisterGPWithNullUser() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            gpService.registerGP(null);
        });
    }

    @Test
    void testRegisterGPWithInvalidRole() {
        // Given
        gp.setRole("INVALID");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            gpService.registerGP(gp);
        });
    }

    @Test
    void testFindGPProches() {
        // Given
        String ville = "Paris";
        double rayonKm = 10.0;
        double[] coordinates = {2.3522, 48.8566}; // Coordonnées de Paris

        when(geocodingService.geocodeAddress(ville)).thenReturn(coordinates);
        when(userRepository.findNearbyGP("GP", coordinates, rayonKm * 1000))
            .thenReturn(List.of(gp));

        // When
        List<User> result = gpService.findGPProches(ville, rayonKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNom());
        assertEquals("GP", result.get(0).getRole());
        verify(userRepository, times(1)).findNearbyGP("GP", coordinates, rayonKm * 1000);
        verify(geocodingService, times(1)).geocodeAddress(ville);
    }

    @Test
    void testFindGPParTrajet() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        double rayonKm = 10.0;
        double[] coordinates = {2.3522, 48.8566}; // Coordonnées de Paris

        when(geocodingService.geocodeAddress(pointDepart)).thenReturn(coordinates);
        when(userRepository.findNearbyGP("GP", coordinates, rayonKm * 1000))
            .thenReturn(List.of(gp));

        // When
        List<User> result = gpService.findGPParTrajet(pointDepart, pointArrivee, rayonKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNom());
        assertEquals("GP", result.get(0).getRole());
        assertEquals("Lyon", result.get(0).getTrajets().get(0).getPointArrivee());
        verify(userRepository, times(1)).findNearbyGP("GP", coordinates, rayonKm * 1000);
        verify(geocodingService, times(1)).geocodeAddress(pointDepart);
    }

    @Test
    void testGetGPById() {
        // Given
        String id = "1";
        when(userRepository.findById(id)).thenReturn(Optional.of(gp));

        // When
        User result = gpService.getGPById(id);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getNom());
        assertEquals("GP", result.getRole());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void testGetGPByIdNotFound() {
        // Given
        String id = "999";
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            gpService.getGPById(id);
        });
    }
} 