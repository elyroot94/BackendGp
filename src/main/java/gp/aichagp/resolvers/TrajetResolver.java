package gp.aichagp.resolvers;

import gp.aichagp.dto.CreateTrajetInput;
import gp.aichagp.dto.SearchTrajetInput;
import gp.aichagp.models.Trajet;
import gp.aichagp.services.TrajetService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TrajetResolver {

    private final TrajetService trajetService;

    @QueryMapping
    public List<Trajet> trajets(@Argument("searchInput") SearchTrajetInput searchInput) {
        if (searchInput == null) {
            return trajetService.searchTrajets(null, null, LocalDateTime.now());
        }
        return trajetService.searchTrajets(
            searchInput.getPointDepart(),
            searchInput.getPointArrivee(),
            searchInput.getDateDepart() != null ? searchInput.getDateDepart() : LocalDateTime.now()
        );
    }

    @QueryMapping
    public Trajet trajet(@Argument("id") String id) {
        return trajetService.getTrajetById(id);
    }

    @MutationMapping
    public Trajet createTrajet(@Argument("input") CreateTrajetInput input) {
        Trajet trajet = new Trajet();
        trajet.setPointDepart(input.getPointDepart());
        trajet.setPointArrivee(input.getPointArrivee());
        trajet.setDateDepart(input.getDateDepart());
        trajet.setDateArriveeEstimee(input.getDateArriveeEstimee());
        trajet.setCapaciteMaxKilos(input.getCapaciteMaxKilos());
        trajet.setKilosDisponibles(input.getKilosDisponibles());
        return trajetService.createTrajet(trajet);
    }
} 