package gp.aichagp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {
    @Id
    private String id;
    private Double montant;
    private String statut;
    private String transactionId;
    private String methodePaiement;
    private Coli colis; // Stocke le colis en tant que sous-document
}