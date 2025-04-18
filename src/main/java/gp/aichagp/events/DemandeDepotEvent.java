package gp.aichagp.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDepotEvent {
    private String colisId;
    private String expediteurId;
    private String gpId;
    private String description; // Inclure une description du colis peut être utile dans la notification
    // Tu peux ajouter d'autres informations pertinentes ici
}