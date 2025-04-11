package gp.aichagp.services;

import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import gp.aichagp.exceptions.GeocodingException;
import org.springframework.web.client.RestClientException;

@Service
public class GeocodingService {

    @Setter
    private RestTemplate restTemplate;
    private final String nominatimUrl;

    public GeocodingService(@Value("${nominatim.url:https://nominatim.openstreetmap.org}") String nominatimUrl) {
        this.nominatimUrl = nominatimUrl;
        this.restTemplate = new RestTemplate();
    }

    public double[] geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new GeocodingException("L'adresse ne peut pas être null ou vide");
        }

        String url = nominatimUrl + "/search?format=json&q=" + address;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "AichaGP/1.0");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<JsonNode> response;
        try {
            response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );
        } catch (RestClientException e) {
            throw new GeocodingException("Erreur lors de l'appel au service de géocodage", e);
        }

        JsonNode body = response.getBody();
        if (body == null) {
            throw new GeocodingException("Réponse vide du service de géocodage pour l'adresse : " + address);
        }

        if (!body.isArray()) {
            throw new GeocodingException("Format de réponse invalide pour l'adresse : " + address);
        }

        if (body.isEmpty()) {
            throw new GeocodingException("Aucun résultat trouvé pour l'adresse : " + address);
        }

        JsonNode location = body.get(0);
        if (location == null || location.isNull()) {
            throw new GeocodingException("Résultat invalide pour l'adresse : " + address);
        }

        JsonNode latNode = location.get("lat");
        JsonNode lonNode = location.get("lon");

        if (latNode == null || lonNode == null || !latNode.isValueNode() || !lonNode.isValueNode()) {
            throw new GeocodingException("Coordonnées manquantes dans la réponse pour l'adresse : " + address);
        }

        try {
            double lat = Double.parseDouble(latNode.asText());
            double lon = Double.parseDouble(lonNode.asText());
            return new double[]{lon, lat};
        } catch (NumberFormatException e) {
            throw new GeocodingException("Format invalide des coordonnées pour l'adresse : " + address, e);
        }
    }
}