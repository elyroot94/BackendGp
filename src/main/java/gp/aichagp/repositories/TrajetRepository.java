package gp.aichagp.repositories;

import gp.aichagp.models.Trajet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrajetRepository extends MongoRepository<Trajet, String> {
    List<Trajet> findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
        String pointDepart, String pointArrivee, String dateDepart);
} 