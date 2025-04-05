package gp.aichagp.resolvers;

import gp.aichagp.exceptions.DateFormatException;
import gp.aichagp.exceptions.TrajetValidationException;
import gp.aichagp.models.Trajet;
import gp.aichagp.models.TrajetInput;
import gp.aichagp.services.TrajetService;
import gp.aichagp.services.GPService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        validateDateFormat(dateDepart);
        return trajetService.searchTrajets(pointDepart, pointArrivee, dateDepart);
    }

    @QueryMapping
    public List<Trajet> searchTrajetsProches(
            @Argument String pointDepart,
            @Argument String pointArrivee,
            @Argument String dateDepart,
            @Argument double rayonKm) {
        validateDateFormat(dateDepart);
        return gpService.findTrajetsProches(pointDepart, pointArrivee, dateDepart, rayonKm);
    }

    @QueryMapping
    public Trajet trajet(@Argument String id) {
        return trajetService.getTrajetById(id);
    }

    @MutationMapping
    public Trajet createTrajet(@Argument("input") TrajetInput input) {
        if (input == null) {
            throw new TrajetValidationException("Les données du trajet sont obligatoires");
        }

        validateDateFormat(input.dateDepart());

        Trajet trajet = new Trajet();
        trajet.setPointDepart(input.pointDepart());
        trajet.setPointArrivee(input.pointArrivee());
        trajet.setCapaciteMaxKilos(input.capaciteMaxKilos());
        trajet.setKilosDisponibles(input.kilosDisponibles());
        trajet.setDateDepart(input.dateDepart());

        return trajetService.createTrajet(trajet);
    }

    private void validateDateFormat(String dateDepart) {
        if (dateDepart == null) {
            throw new TrajetValidationException("La date de départ est obligatoire");
        }
        try {
            LocalDateTime.parse(dateDepart, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new TrajetValidationException("Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)");
        }
    }
} 