package gp.aichagp.unitaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gp.aichagp.exceptions.GeocodingException;
import gp.aichagp.services.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    private GeocodingService geocodingService;

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;
    private static final String TEST_URL = "https://nominatim.openstreetmap.org";
    private static final String TEST_ADDRESS = "Paris, France";

    @BeforeEach
    void setUp() {
        geocodingService = new GeocodingService(TEST_URL);
        geocodingService.setRestTemplate(restTemplate);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGeocodeAddress_Success() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        ObjectNode locationNode = objectMapper.createObjectNode()
                .put("lat", "48.8566")
                .put("lon", "2.3522");
        responseBody.add(locationNode);

        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When
        double[] coordinates = geocodingService.geocodeAddress(TEST_ADDRESS);

        // Then
        assertEquals(2.3522, coordinates[0], 0.0001); // longitude
        assertEquals(48.8566, coordinates[1], 0.0001); // latitude
        verify(restTemplate).exchange(
            contains(TEST_ADDRESS),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        );
    }

    @Test
    void testGeocodeAddress_NullAddress() {
        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(null);
        });
        assertEquals("L'adresse ne peut pas être null ou vide", exception.getMessage());
        verify(restTemplate, never()).exchange(
            any(String.class),
            any(HttpMethod.class),
            any(HttpEntity.class),
            any(Class.class)
        );
    }

    @Test
    void testGeocodeAddress_EmptyAddress() {
        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress("  ");
        });
        assertEquals("L'adresse ne peut pas être null ou vide", exception.getMessage());
        verify(restTemplate, never()).exchange(
            any(String.class),
            any(HttpMethod.class),
            any(HttpEntity.class),
            any(Class.class)
        );
    }

    @Test
    void testGeocodeAddress_ApiError() {
        // Given
        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenThrow(new RestClientException("API Error"));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Erreur lors de l'appel au service de géocodage", exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NullResponse() {
        // Given
        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Réponse vide du service de géocodage pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NotAnArray() {
        // Given
        ObjectNode nonArrayResponse = objectMapper.createObjectNode()
                .put("error", "not an array");
        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(nonArrayResponse, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Format de réponse invalide pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_EmptyResults() {
        // Given
        ArrayNode emptyResponse = objectMapper.createArrayNode();
        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(emptyResponse, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Aucun résultat trouvé pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NullLocation() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        responseBody.addNull(); // Ajoute un élément JSON null
        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Résultat invalide pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_LocationWithNullValues() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        ObjectNode locationNode = objectMapper.createObjectNode()
                .put("lat", "null")  // Notez que c'est une chaîne "null" et non un null réel
                .put("lon", "null"); // Idem
        responseBody.add(locationNode);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Format invalide des coordonnées pour l'adresse : " + TEST_ADDRESS,
                exception.getMessage());
    }

    @Test
    void testGeocodeAddress_MissingCoordinates() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        ObjectNode locationNode = objectMapper.createObjectNode(); // Sans lat/lon
        responseBody.add(locationNode);

        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Coordonnées manquantes dans la réponse pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_InvalidCoordinateFormat() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        ObjectNode locationNode = objectMapper.createObjectNode()
                .put("lat", "invalid")
                .put("lon", "invalid");
        responseBody.add(locationNode);

        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Format invalide des coordonnées pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }

    @Test
    void testGeocodeAddress_NonValueNodeCoordinates() {
        // Given
        ArrayNode responseBody = objectMapper.createArrayNode();
        ObjectNode locationNode = objectMapper.createObjectNode();
        locationNode.putArray("lat");  // Crée un tableau vide au lieu d'une valeur
        locationNode.putObject("lon"); // Crée un objet vide au lieu d'une valeur
        responseBody.add(locationNode);

        when(restTemplate.exchange(
            any(String.class),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // When & Then
        GeocodingException exception = assertThrows(GeocodingException.class, () -> {
            geocodingService.geocodeAddress(TEST_ADDRESS);
        });
        assertEquals("Coordonnées manquantes dans la réponse pour l'adresse : " + TEST_ADDRESS, 
                    exception.getMessage());
    }
}