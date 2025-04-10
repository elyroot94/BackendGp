package gp.aichagp.repositories;

import gp.aichagp.models.Trajet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrajetRepository extends MongoRepository<Trajet, String> {
    List<Trajet> findByPointDepartAndPointArriveeAndDateDepartGreaterThanEqual(
        String pointDepart, String pointArrivee, LocalDateTime dateDepart);
    List<Trajet> findByGpId(String gpId);

    @Query("{ 'gpId': { $in: ?0 }, 'pointDepart': ?1, 'pointArrivee': ?2 }")
    List<Trajet> findTrajetsForGPs(List<String> gpIds, String pointDepart, String pointArrivee);
} 