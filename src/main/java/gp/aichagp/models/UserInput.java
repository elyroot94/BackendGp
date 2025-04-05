package gp.aichagp.models;

public record UserInput(
    String nom,
    String prenom,
    String email,
    String telephone,
    String password,
    String role,
    String adresse
) {} 