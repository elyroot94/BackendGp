package gp.aichagp.resolvers;

import gp.aichagp.models.User;
import gp.aichagp.models.UserInput;
import gp.aichagp.services.GPService;
import gp.aichagp.exceptions.UserRegistrationException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GPResolver {
    
    private final GPService gpService;
    
    public GPResolver(GPService gpService) {
        this.gpService = gpService;
    }
    
    @QueryMapping
    public List<User> searchGPProches(
            @Argument String ville,
            @Argument double rayonKm) {
        return gpService.findGPProches(ville, rayonKm);
    }
    
    @QueryMapping
    public List<User> searchGPParTrajet(
            @Argument String pointDepart,
            @Argument String pointArrivee,
            @Argument double rayonKm) {
        return gpService.findGPParTrajet(pointDepart, pointArrivee, rayonKm);
    }
    
    @QueryMapping
    public User getGPById(@Argument String id) {
        return gpService.getGPById(id);
    }

    @MutationMapping
    public User registerGP(@Argument("input") UserInput input) {
        try {
            User gp = new User();
            gp.setNom(input.nom());
            gp.setPrenom(input.prenom());
            gp.setEmail(input.email());
            gp.setTelephone(input.telephone());
            gp.setPassword(input.password());
            gp.setRole(input.role());
            gp.setAdresse(input.adresse());
            return gpService.registerGP(gp);
        } catch (UserRegistrationException e) {
            throw e;
        } catch (Exception e) {
            throw new UserRegistrationException("Erreur lors de l'enregistrement du GP : " + e.getMessage());
        }
    }
} 