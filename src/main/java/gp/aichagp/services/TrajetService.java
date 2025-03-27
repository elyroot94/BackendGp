package gp.aichagp.services;

import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.TrajetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrajetService {

    private final TrajetRepository trajetRepository;

    public TrajetService(TrajetRepository trajetRepository) {
        this.trajetRepository = trajetRepository;
    }

    public Trajet createTrajet(Trajet trajet) {
        validateTrajet(trajet);
        return trajetRepository.save(trajet);
    }

    public List<Trajet> searchTrajets(String pointDepart, String pointArrivee, LocalDateTime dateDepart) {
        return trajetRepository.findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
            pointDepart, pointArrivee, dateDepart);
    }

    public Trajet getTrajetById(String id) {
        return trajetRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trajet non trouvé avec l'ID: " + id));
    }

    private void validateTrajet(Trajet trajet) {
        if (trajet.getDateDepart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de départ ne peut pas être dans le passé");
        }
        if (trajet.getCapaciteMaxKilos() <= 0 || trajet.getKilosDisponibles() <= 0) {
            throw new IllegalArgumentException("La capacité et les kilos disponibles doivent être positifs");
        }
        if (trajet.getKilosDisponibles() > trajet.getCapaciteMaxKilos()) {
            throw new IllegalArgumentException("Les kilos disponibles ne peuvent pas dépasser la capacité maximale");
        }
    }
} 