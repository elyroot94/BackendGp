package gp.aichagp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SearchTrajetInput {
    private String pointDepart;
    private String pointArrivee;
    private LocalDateTime dateDepart;
} 