package gp.aichagp.integration.services;

import gp.aichagp.models.Trajet;
import gp.aichagp.models.User;
import gp.aichagp.services.GeocodingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Testcontainers
public class GeocodingServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.2")
            .withReuse(true)
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> 
            String.format("mongodb://%s:%d/test", 
                mongoDBContainer.getHost(), 
                mongoDBContainer.getFirstMappedPort()));
    }

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start();
        }
        // Attendre que le conteneur soit prêt
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Nettoyer la base de données avant chaque test
        mongoTemplate.getDb().drop();
        
        // Créer les collections nécessaires
        mongoTemplate.createCollection(User.class);
        mongoTemplate.createCollection(Trajet.class);
        
        // Créer l'index géospatial
        mongoTemplate.indexOps(User.class).ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE));
    }

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
        String invalidAddress = "Invalid Address 123456789";

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            geocodingService.geocodeAddress(invalidAddress);
        });
    }
} 