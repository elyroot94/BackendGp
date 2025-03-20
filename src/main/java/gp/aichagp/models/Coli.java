package gp.aichagp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "colis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coli {
    @Id
    private String id;
    private Double poids;
    private String description;
    private String destinataireNom;
    private String destinataireTelephone;
    private String statut;
    private String qrCode;
    private String codeUnique;
    private String expediteurId;
    private User gp; // Stocke le GP en tant que sous-document
}
