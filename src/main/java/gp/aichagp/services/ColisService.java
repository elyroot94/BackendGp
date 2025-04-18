package gp.aichagp.services;

import gp.aichagp.events.DemandeDepotEvent;
import gp.aichagp.models.Coli;
import gp.aichagp.repositories.ColisRepository;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ColisService {

    private final ColisRepository colisRepository;
    private final KafkaTemplate<String, DemandeDepotEvent> kafkaTemplate;
    private static final String DEMANDES_DEPOT_TOPIC = "demandes-depot";

    public ColisService(ColisRepository colisRepository, KafkaTemplate<String, DemandeDepotEvent> kafkaTemplate) {
        this.colisRepository = colisRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Coli creerColis(Coli coli) {
        Coli nouveauColis = colisRepository.save(coli);
        // Publier un événement après la création du colis
        DemandeDepotEvent demandeDepotEvent = new DemandeDepotEvent(
                nouveauColis.getId(),
                nouveauColis.getExpediteurId(),
                nouveauColis.getGpId(),
                nouveauColis.getDescription()
        );
        kafkaTemplate.send(DEMANDES_DEPOT_TOPIC, demandeDepotEvent);
        return nouveauColis;
    }

    // Autres méthodes de gestion des colis...
}