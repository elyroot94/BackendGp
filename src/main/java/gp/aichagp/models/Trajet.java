package gp.aichagp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "trajets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trajet {
    @Id
    private String id;
    private String pointDepart;
    private String pointArrivee;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArriveeEstimee;
    private List<Coli> listeColis; // Stocker les colis en tant que sous-documents
}