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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Id
    private String id;
    private String pointDepart;
    private String pointArrivee;
    private String dateDepart;
    private String dateArriveeEstimee;
    private List<Coli> listeColis; // Stocker les colis en tant que sous-documents
    private Double capaciteMaxKilos; // Utilisé uniquement si role = "GP"
    private Double kilosDisponibles;
    private User gp; // Référence au GP qui propose ce trajet

    public LocalDateTime getDateDepartAsDateTime() {
        if (dateDepart == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateDepart, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateFormatException("Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)");
        }
    }

    public LocalDateTime getDateArriveeEstimeeAsDateTime() {
        if (dateArriveeEstimee == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateArriveeEstimee, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new DateFormatException("Le format de la date doit être YYYY-MM-DDTHH:mm:ss (exemple: 2024-03-29T10:00:00)");
        }
    }
}

