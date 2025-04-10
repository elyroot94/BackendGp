package gp.aichagp.models;

import gp.aichagp.exceptions.DateFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private Double capaciteMaxKilos; // Utilisé uniquement si role = "GP"
    private Double kilosDisponibles;
    private String gpId; // Référence au GP qui propose ce trajet

    public String getDateDepart() {
        return dateDepart != null ?
                dateDepart.format(DateTimeFormatter.ISO_DATE_TIME) :
                null;
    }
}

