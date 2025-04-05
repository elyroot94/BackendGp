package gp.aichagp.unitaires;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gp.aichagp.services.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeocodingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private GeocodingService geocodingService;

    private final String testAddress = "Tour Eiffel, Paris";
    private final double[] expectedCoordinates = {2.2945, 48.8584};

    @BeforeEach
    void setUp() {
        geocodingService = new GeocodingService("https://nominatim.openstreetmap.org");
        geocodingService.setRestTemplate(restTemplate);
    }

    @Test
    void testGeocodeAddressSuccess() throws Exception {
        // Given
        String jsonResponse = "[{\"lat\":\"48.8584\",\"lon\":\"2.2945\"}]";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(jsonResponse);
        
        ResponseEntity<JsonNode> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(responseNode);
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(responseEntity);

        // When
        double[] coordinates = geocodingService.geocodeAddress(testAddress);

        // Then
        assertNotNull(coordinates);
        assertEquals(2, coordinates.length);
        assertEquals(expectedCoordinates[0], coordinates[0], 0.0001);
        assertEquals(expectedCoordinates[1], coordinates[1], 0.0001);
    }

    @Test
    void testGeocodeAddressEmptyResponse() {
        // Given
        ResponseEntity<JsonNode> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(null);
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(responseEntity);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            geocodingService.geocodeAddress(testAddress);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de géocoder l'adresse"));
    }

    @Test
    void testGeocodeAddressInvalidResponse() throws Exception {
        // Given
        String jsonResponse = "[]";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(jsonResponse);
        
        ResponseEntity<JsonNode> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(responseNode);
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(JsonNode.class)
        )).thenReturn(responseEntity);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            geocodingService.geocodeAddress(testAddress);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de géocoder l'adresse"));
    }
} 