package gp.aichagp.services;

import gp.aichagp.events.DemandeDepotEvent;
import gp.aichagp.models.Notification;
import gp.aichagp.repositories.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GpNotificationService {

    private final NotificationRepository notificationRepository;
    // Ici, tu pourrais avoir d'autres services pour l'envoi de notifications (EmailService, PushNotificationService, etc.)

    public GpNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "demandes-depot", groupId = "notifications-gp-group")
    public void traiterNouvelleDemandeDepot(DemandeDepotEvent event) {
        String gpId = event.getGpId();
        String message = "Nouvelle demande de dépôt de colis #" + event.getColisId() + " de la part de l'utilisateur #" + event.getExpediteurId() + ". Description : " + event.getDescription();

        // Enregistrer la notification dans la base de données
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setDate(LocalDateTime.now());
        notification.setUtilisateurId(gpId);
        notificationRepository.save(notification);

        // Ici, tu pourrais également appeler un service pour envoyer une notification en temps réel au GP
        // (par exemple, via un service de push notifications si l'application GP est connectée).
        System.out.println("Notification enregistrée pour le GP " + gpId + ": " + message);
    }
}