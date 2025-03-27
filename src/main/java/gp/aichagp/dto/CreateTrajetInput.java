package gp.aichagp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateTrajetInput {
    private String pointDepart;
    private String pointArrivee;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArriveeEstimee;
    private Double capaciteMaxKilos;
    private Double kilosDisponibles;
} 