package gp.aichagp.repositories;

import gp.aichagp.models.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'role': ?0, 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: ?1 }, $maxDistance: ?2 } } }")
    List<User> findNearbyGP(String role, double[] coordinates, double maxDistance);
    
    List<User> findByRole(String role);
    
    Optional<User> findByEmail(String email);


    @Aggregation(pipeline = {
            "{ $geoNear: { "
                    + "near: { type: 'Point', coordinates: ?0 }, "
                    + "distanceField: 'distance', " // Champ temporaire pour stocker la distance
                    + "maxDistance: ?1, " // En mètres
                    + "spherical: true, " // Pour calculs géodésiques
                    + "query: { role: 'GP' } " // Filtre supplémentaire
                    + "} }",

            // Étape 2: Joindre les trajets correspondants
            "{ $lookup: { "
                    + "from: 'trajets', "
                    + "let: { userId: '$_id' }, "
                    + "pipeline: [ "
                    + "{ $match: { "
                    + "$expr: { $eq: ['$gpId', '$$userId'] }, "
                    + "pointDepart: ?2, "
                    + "pointArrivee: ?3 "
                    + "} } "
                    + "], "
                    + "as: 'trajetsFiltres' "
                    + "} }",

            // Étape 3: Filtrer les GP avec au moins 1 trajet valide
            "{ $match: { 'trajetsFiltres.0': { $exists: true } } }"
    })
    List<User> findNearbyGPWithMatchingTrajet(
            double[] coordinates,  // [longitude, latitude]
            double radiusInRadians, // Convertir km → radians avec (km / 6378.1)
            String pointDepart,
            String pointArrivee
    );
}