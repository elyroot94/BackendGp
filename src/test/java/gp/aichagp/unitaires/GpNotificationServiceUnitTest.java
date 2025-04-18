package gp.aichagp.unitaires;

import gp.aichagp.events.DemandeDepotEvent;
import gp.aichagp.models.Notification;
import gp.aichagp.repositories.NotificationRepository;
import gp.aichagp.services.GpNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GpNotificationServiceUnitTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private GpNotificationService gpNotificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Test
    void traiterNouvelleDemandeDepot_shouldSaveNotificationToRepository() {
        // Arrange
        DemandeDepotEvent event = new DemandeDepotEvent(
                "colis123",
                "client456",
                "gp789",
                "Fragile package"
        );

        // Act
        gpNotificationService.traiterNouvelleDemandeDepot(event);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals("Nouvelle demande de dépôt de colis #colis123 de la part de l'utilisateur #client456. Description : Fragile package", savedNotification.getMessage());
        assertEquals("gp789", savedNotification.getUtilisateurId());
        assertNotNull(savedNotification.getDate());
    }

    @Test
    void traiterNouvelleDemandeDepot_shouldSaveNotificationWithCorrectDetails() {
        // Arrange
        DemandeDepotEvent event = new DemandeDepotEvent(
                "parcel-xyz",
                "sender-abc",
                "transporter-def",
                "Important documents"
        );

        // Act
        gpNotificationService.traiterNouvelleDemandeDepot(event);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals("Nouvelle demande de dépôt de colis #parcel-xyz de la part de l'utilisateur #sender-abc. Description : Important documents", savedNotification.getMessage());
        assertEquals("transporter-def", savedNotification.getUtilisateurId());
        assertNotNull(savedNotification.getDate());
    }
}