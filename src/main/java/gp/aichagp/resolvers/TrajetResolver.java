package gp.aichagp.resolvers;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.dto.TrajetInput;

import gp.aichagp.services.TrajetService;
import gp.aichagp.services.GPService;
import org.springframework.graphql.data.method.annotation.Argument;

import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TrajetResolver {

    private final TrajetService trajetService;
    private final GPService gpService;



    public TrajetResolver(TrajetService trajetService, GPService gpService) {
        this.trajetService = trajetService;
        this.gpService = gpService;


    }

    @QueryMapping
    public List<Trajet> getAllTrajets() {
        return trajetService.getAllTrajets();
    }

    @QueryMapping
    public List<Trajet> searchTrajets(
            @Argument String pointDepart,
            @Argument String pointArrivee,
            @Argument String dateDepart) {
        return trajetService.searchTrajets(pointDepart, pointArrivee, dateDepart);
    }

    @QueryMapping
    public List<Trajet> searchTrajetsProches(
            @Argument String pointDepart,
            @Argument String pointArrivee,
            @Argument String dateDepart,
            @Argument double rayonKm) {

        return gpService.findTrajetsProches(pointDepart, pointArrivee, dateDepart, rayonKm);
    }

    @QueryMapping
    public Trajet trajet(@Argument String id) {
        return trajetService.getTrajetById(id);
    }

    /**
     * Crée un nouveau trajet.
     *
     * @param input Les données du trajet
     * @return Le trajet créé et sauvegardé
     * @throws TrajetValidationException Si les données du trajet sont invalides
     * @throws DateFormatException Si le format de la date est incorrect
     */
    @MutationMapping
    public Trajet createTrajet(@Argument("input") TrajetInput input) {

        // Vérification que les données du trajet sont présentes
        if (input == null) {
            throw new TrajetValidationException("Les données du trajet sont obligatoires");
        }


        // Validation date future
        if (input.dateDepart().isBefore(LocalDateTime.now())) {
            throw new TrajetValidationException("La date de départ ne peut pas être dans le passé");
        }

        // Création du trajet
        Trajet trajet = new Trajet();
        trajet.setPointDepart(input.pointDepart());
        trajet.setPointArrivee(input.pointArrivee());
        trajet.setDateDepart(input.dateDepart()); // LocalDateTime directement
        trajet.setCapaciteMaxKilos(input.capaciteMaxKilos());
        trajet.setKilosDisponibles(input.kilosDisponibles());
        trajet.setGpId(input.gpId());

        return trajetService.createTrajet(trajet);
    }


} 