package gp.aichagp.dto;

public record TrajetInput(
    String pointDepart,
    String pointArrivee,
    String dateDepart,
    double capaciteMaxKilos,
    double kilosDisponibles,
    String  gpId
) {} 