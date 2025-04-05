package gp.aichagp.integration.services;

import gp.aichagp.services.GeocodingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class GeocodingServiceIntegrationTest {

    @Autowired
    private GeocodingService geocodingService;

    @Test
    public void shouldGeocodeValidAddress() {
        // Given
        String address = "Tour Eiffel, Paris";

        // When
        double[] coordinates = geocodingService.geocodeAddress(address);

        // Then
        assertNotNull(coordinates);
        assertEquals(2, coordinates.length);
        assertTrue(coordinates[0] >= -180 && coordinates[0] <= 180); // longitude
        assertTrue(coordinates[1] >= -90 && coordinates[1] <= 90);   // latitude
    }

    @Test
    public void shouldThrowExceptionForInvalidAddress() {
        // Given
        String invalidAddress = "InvalidAddress123456789";

        // When/Then
        assertThrows(RuntimeException.class, () -> {
            geocodingService.geocodeAddress(invalidAddress);
        });
    }
} 