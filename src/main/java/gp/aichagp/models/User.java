package gp.aichagp.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;


import java.util.ArrayList;
import java.util.List;

@Document(collection = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;
    private String role; // "GP" ou "Expéditeur"
    private Boolean verificationIdentite = false;
    private String documentsIdentite;
    @DBRef
    private List<Trajet> trajets = new ArrayList<>();
    private List<Coli> listeColisAcceptes = new ArrayList<>();
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location; // [longitude, latitude]
    private String adresse;


}
