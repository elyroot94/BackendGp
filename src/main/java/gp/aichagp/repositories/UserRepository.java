package gp.aichagp.repositories;

import gp.aichagp.models.User;
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
} 