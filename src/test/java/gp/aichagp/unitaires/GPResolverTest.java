package gp.aichagp.unitaires;

import gp.aichagp.models.User;
import gp.aichagp.dto.UserInput;
import gp.aichagp.resolvers.GPResolver;
import gp.aichagp.services.GPService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GPResolverTest {

    @Mock
    private GPService gpService;

    @InjectMocks
    private GPResolver gpResolver;

    private User gp;

    @BeforeEach
    void setUp() {
        gp = new User();
        gp.setId("1");
        gp.setNom("John");
        gp.setPrenom("Doe");
        gp.setEmail("john.doe@example.com");
        gp.setRole("GP");
    }

    @Test
    void testSearchGPProches() {
        // Given
        String ville = "Paris";
        double rayonKm = 10.0;

        when(gpService.findGPProches(ville, rayonKm))
            .thenReturn(List.of(gp));

        // When
        List<User> result = gpResolver.searchGPProches(ville, rayonKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getNom());
        assertEquals("GP", result.getFirst().getRole());
    }

    @Test
    void testSearchGPParTrajet() {
        // Given
        String pointDepart = "Paris";
        String pointArrivee = "Lyon";
        double rayonKm = 10.0;

        when(gpService.findGPParTrajet(pointDepart, pointArrivee, rayonKm))
            .thenReturn(List.of(gp));

        // When
        List<User> result = gpResolver.searchGPParTrajet(pointDepart, pointArrivee, rayonKm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getNom());
        assertEquals("GP", result.getFirst().getRole());
    }

    @Test
    void testGetGPById() {
        // Given
        String id = "1";
        when(gpService.getGPById(id)).thenReturn(gp);

        // When
        User result = gpResolver.getGPById(id);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getNom());
        assertEquals("GP", result.getRole());
    }

    @Test
    void testRegisterGP() {
        // Given
        UserInput input = new UserInput(
            "Jane",
            "Doe",
            "jane.doe@example.com",
            "0123456789",
            "password",
            "GP",
            "Paris"
        );

        when(gpService.registerGP(any(User.class))).thenReturn(gp);

        // When
        User result = gpResolver.registerGP(input);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getNom());
        assertEquals("GP", result.getRole());
    }
} 