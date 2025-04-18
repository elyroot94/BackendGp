package gp.aichagp.repositories;

import gp.aichagp.models.Coli;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisRepository extends MongoRepository<Coli, String> {
}
