package nr.gql.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import nr.gql.models.Persona;

public interface RPersona extends MongoRepository<Persona, String> {

}
