package gp.aichagp.services;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;

import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import java.util.List;

/**
 * Service gérant les opérations liées aux trajets.
 * Responsable de la validation et de la persistance des trajets.
 */
@Service
public class TrajetService {




    private final TrajetRepository trajetRepository;
    private final UserRepository userRepository;
    // Messages d'erreur
    private static final String ERROR_TRAJET_NULL = "Le trajet ne peut pas être null";
    private static final String ERROR_POINT_DEPART_OBLIGATOIRE = "Le point de départ est obligatoire";
    private static final String ERROR_POINT_ARRIVEE_OBLIGATOIRE = "Le point d'arrivée est obligatoire";
    private static final String ERROR_CAPACITE_MAX_INVALIDE = "La capacité maximale doit être supérieure à 0";
    private static final String ERROR_KILOS_DISPONIBLES_INVALIDE = "Les kilos disponibles doivent être supérieurs à 0";
    private static final String ERROR_KILOS_DEPASSENT_CAPACITE = "Les kilos disponibles ne peuvent pas dépasser la capacité maximale";

    public TrajetService(TrajetRepository trajetRepository, UserRepository userRepository) {
        this.trajetRepository = trajetRepository;
        this.userRepository = userRepository;
    }


    /**
     * Crée un nouveau trajet après validation.
     *
     * @param trajet Le trajet à créer
     * @return Le trajet créé et sauvegardé
     * @throws TrajetValidationException Si les données du trajet sont invalides
     * @throws DateFormatException Si le format de la date est incorrect
     */
    public Trajet createTrajet(Trajet trajet) {
        // Validation du trajet null en premier
        validateTrajet(trajet);
        
        // Validation GP existe
        if (!userRepository.existsById(trajet.getGpId())) {
            throw new TrajetValidationException("GP non trouvé: " + trajet.getGpId());
        }

        validatePoints(trajet);
        validateKilos(trajet);
        return trajetRepository.save(trajet);
    }

    /**
     * Récupère tous les trajets.
     *
     * @return La liste des trajets
     */
    public List<Trajet> getAllTrajets() {
        return trajetRepository.findAll();
    }

    /**
     * Recherche des trajets selon des critères.
     *
     * @param pointDepart Le point de départ
     * @param pointArrivee Le point d'arrivée
     * @param dateDepart La date de départ minimale
     * @return La liste des trajets correspondant aux critères
     */
    public List<Trajet> searchTrajets(String pointDepart, String pointArrivee, String dateDepart) {
        if (pointDepart == null || pointDepart.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point de départ ne peut pas être null ou vide");
        }
        if (pointArrivee == null || pointArrivee.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point d'arrivée ne peut pas être null ou vide");
        }
        if (dateDepart == null) {
            throw new IllegalArgumentException("La date de départ ne peut pas être null");
        }

        LocalDateTime dateDepartParsed;
        try {
            dateDepartParsed = LocalDateTime.parse(dateDepart);
        } catch (DateTimeParseException e) {
            throw new DateFormatException("Format de date invalide. Utilisez le format ISO-8601 (ex: 2025-12-31T10:00:00)");
        }

        return trajetRepository.findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
            pointDepart, pointArrivee, dateDepartParsed);
    }

    /**
     * Récupère un trajet par son ID.
     *
     * @param id L'identifiant du trajet
     * @return Le trajet trouvé
     * @throws IllegalArgumentException Si le trajet n'est pas trouvé
     */
    public Trajet getTrajetById(String id) {
        return trajetRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Trajet non trouvé avec l'ID: " + id));
    }

    /**
     * Valide un trajet selon toutes les règles métier.
     *
     * @param trajet Le trajet à valider
     * @throws TrajetValidationException Si les données du trajet sont invalides
     * @throws DateFormatException Si le format de la date est incorrect
     */
    private void validateTrajet(Trajet trajet) {
        validateTrajetNotNull(trajet);
        validatePoints(trajet);
        validateKilos(trajet);
    }

    private void validateTrajetNotNull(Trajet trajet) {
        if (trajet == null) {
            throw new TrajetValidationException(ERROR_TRAJET_NULL);
        }
    }

    private void validatePoints(Trajet trajet) {
        if (trajet.getPointDepart() == null || trajet.getPointDepart().trim().isEmpty()) {
            throw new TrajetValidationException(ERROR_POINT_DEPART_OBLIGATOIRE);
        }

        if (trajet.getPointArrivee() == null || trajet.getPointArrivee().trim().isEmpty()) {
            throw new TrajetValidationException(ERROR_POINT_ARRIVEE_OBLIGATOIRE);
        }
    }


    private void validateKilos(Trajet trajet) {
        if (trajet.getCapaciteMaxKilos() == null || trajet.getCapaciteMaxKilos() <= 0) {
            throw new TrajetValidationException(ERROR_CAPACITE_MAX_INVALIDE);
        }

        if (trajet.getKilosDisponibles() == null || trajet.getKilosDisponibles() <= 0) {
            throw new TrajetValidationException(ERROR_KILOS_DISPONIBLES_INVALIDE);
        }

        if (trajet.getKilosDisponibles() > trajet.getCapaciteMaxKilos()) {
            throw new TrajetValidationException(ERROR_KILOS_DEPASSENT_CAPACITE);
        }
    }
} 