package gp.aichagp.models;

public record TrajetInput(
    String pointDepart,
    String pointArrivee,
    String dateDepart,
    double capaciteMaxKilos,
    double kilosDisponibles
) {} 