package gp.aichagp.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeocodingService {
    
    private RestTemplate restTemplate;
    private final String nominatimUrl;
    
    public GeocodingService(@Value("${nominatim.url:https://nominatim.openstreetmap.org}") String nominatimUrl) {
        this.nominatimUrl = nominatimUrl;
        this.restTemplate = new RestTemplate();
    }
    
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public double[] geocodeAddress(String address) {
        String url = nominatimUrl + "/search?format=json&q=" + address;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "AichaGP/1.0");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
        
        if (response.getBody() != null && response.getBody().isArray() && response.getBody().size() > 0) {
            JsonNode location = response.getBody().get(0);
            double lat = location.get("lat").asDouble();
            double lon = location.get("lon").asDouble();
            return new double[]{lon, lat};
        }
        
        throw new RuntimeException("Impossible de géocoder l'adresse : " + address);
    }
} 