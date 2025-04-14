package gp.aichagp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TrajetInput(
    String pointDepart,
    String pointArrivee,
    LocalDateTime dateDepart,
    double capaciteMaxKilos,
    double kilosDisponibles,
    String  gpId
) {} 