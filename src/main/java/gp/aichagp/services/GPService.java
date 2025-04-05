package gp.aichagp.services;

import gp.aichagp.models.User;
import gp.aichagp.models.Trajet;
import gp.aichagp.repositories.UserRepository;
import gp.aichagp.repositories.TrajetRepository;
import gp.aichagp.exceptions.UserRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GPService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    private final UserRepository userRepository;
    private final TrajetRepository trajetRepository;
    private final GeocodingService geocodingService;
    
    public GPService(UserRepository userRepository, 
                    TrajetRepository trajetRepository,
                    GeocodingService geocodingService) {
        this.userRepository = userRepository;
        this.trajetRepository = trajetRepository;
        this.geocodingService = geocodingService;
    }
    
    public User registerGP(User gp) {
        if (gp == null) {
            throw new IllegalArgumentException("Le GP ne peut pas être null");
        }

        if (gp.getEmail() == null || gp.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        if (!"GP".equals(gp.getRole())) {
            throw new IllegalArgumentException("Le rôle doit être 'GP'");
        }

        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(gp.getEmail()).isPresent()) {
            throw new UserRegistrationException("Un utilisateur avec cet email existe déjà");
        }

        try {
            // Géocoder l'adresse du GP
            double[] coordinates = geocodingService.geocodeAddress(gp.getAdresse());
            gp.setLocation(coordinates);
            return userRepository.save(gp);
        } catch (RuntimeException e) {
            throw new UserRegistrationException("Impossible de géocoder l'adresse : " + e.getMessage());
        }
    }

    public List<User> findGPProches(String ville, double rayonKm) {
        if (ville == null || ville.trim().isEmpty()) {
            throw new IllegalArgumentException("La ville ne peut pas être null ou vide");
        }

        if (rayonKm <= 0) {
            throw new IllegalArgumentException("Le rayon doit être supérieur à 0");
        }

        // Récupération des coordonnées de la ville (longitude, latitude)
        double[] coordinates = geocodingService.geocodeAddress(ville);

        // Conversion du rayon en mètres
        double rayonMetres = rayonKm * 1000;

        // Appel au repository avec les paramètres corrects
        return userRepository.findNearbyGP("GP", coordinates, rayonMetres);
    }

    public List<User> findGPParTrajet(String pointDepart, String pointArrivee, double rayonKm) {
        if (pointDepart == null || pointDepart.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point de départ ne peut pas être null ou vide");
        }

        if (pointArrivee == null || pointArrivee.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point d'arrivée ne peut pas être null ou vide");
        }

        if (rayonKm <= 0) {
            throw new IllegalArgumentException("Le rayon doit être supérieur à 0");
        }

        // Géocoder le point de départ
        double[] coordinates = geocodingService.geocodeAddress(pointDepart);
        double distance = rayonKm * 1000;
        
        // Trouver les GP proches du point de départ
        List<User> gpsProches = userRepository.findNearbyGP("GP", coordinates, distance);

        // Vérifier que gpsProches contient des trajets non nuls
        return gpsProches.stream()
                .filter(gp -> gp.getTrajets() != null && !gp.getTrajets().isEmpty())
                .filter(gp -> gp.getTrajets().stream()
                        .anyMatch(trajet -> trajet.getPointArrivee().equals(pointArrivee)))
                .collect(Collectors.toList());
    }
    
    public User getGPById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID ne peut pas être null ou vide");
        }

        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("GP non trouvé avec l'ID : " + id));
    }
    
    public List<Trajet> findTrajetsProches(String pointDepart, 
                                         String pointArrivee, 
                                         String dateDepart,
                                         double rayonKm) {
        if (pointDepart == null || pointDepart.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point de départ ne peut pas être null ou vide");
        }

        if (pointArrivee == null || pointArrivee.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point d'arrivée ne peut pas être null ou vide");
        }

        if (dateDepart == null) {
            throw new IllegalArgumentException("La date de départ ne peut pas être null");
        }

        if (rayonKm <= 0) {
            throw new IllegalArgumentException("Le rayon doit être supérieur à 0");
        }

        // Géocoder le point de départ
        double[] coordinates = geocodingService.geocodeAddress(pointDepart);
        double distance = rayonKm * 1000;
        
        // Trouver les GP proches
        List<User> gpsProches = userRepository.findNearbyGP("GP", coordinates, distance);

        // Récupérer les trajets de ces GP
        return gpsProches.stream()
            .flatMap(gp -> trajetRepository.findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
                pointDepart, pointArrivee, dateDepart).stream())
            .collect(Collectors.toList());
    }
} 