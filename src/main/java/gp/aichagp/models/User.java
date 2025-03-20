package gp.aichagp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role; // "GP" ou "Expéditeur"
    private boolean verificationIdentite;
    private String documentsIdentite;
    private Double capaciteMaxKilos; // Utilisé uniquement si role = "GP"
    private Double kilosDisponibles;
    private List<Trajet> trajets;
    private List<Coli> listeColisAcceptes;
}
