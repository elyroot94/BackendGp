package gp.aichagp.unitaires;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import gp.aichagp.models.Coli;
import gp.aichagp.repositories.ColisRepository;
import gp.aichagp.services.ColisService;
import gp.aichagp.events.DemandeDepotEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

class ColisServiceTest {

    private ColisRepository colisRepository;
    private KafkaTemplate<String, DemandeDepotEvent> kafkaTemplate;
    private ColisService colisService;

    @BeforeEach
    void setUp() {
        colisRepository = mock(ColisRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        colisService = new ColisService(colisRepository, kafkaTemplate);
    }

    @Test
    void testCreerColis() {
        // Arrange
        Coli colis = new Coli(); // Initialize with necessary data
        colis.setId(String.valueOf(1L)); // Example ID
        colis.setExpediteurId(String.valueOf(123L));
        colis.setGpId(String.valueOf(456L));
        colis.setDescription("Test colis");

        when(colisRepository.save(colis)).thenReturn(colis);

        // Act
        Coli result = colisService.creerColis(colis);

        // Assert
        assertNotNull(result);
        assertEquals(colis.getId(), result.getId());

        // Verify that the event was published
        ArgumentCaptor<DemandeDepotEvent> eventCaptor = ArgumentCaptor.forClass(DemandeDepotEvent.class);
        verify(kafkaTemplate).send(eq("demandes-depot"), eventCaptor.capture());
        DemandeDepotEvent publishedEvent = eventCaptor.getValue();
        assertEquals(colis.getId(), publishedEvent.getColisId());
        assertEquals(colis.getExpediteurId(), publishedEvent.getExpediteurId());
        assertEquals(colis.getGpId(), publishedEvent.getGpId());
        assertEquals(colis.getDescription(), publishedEvent.getDescription());
    }
}
