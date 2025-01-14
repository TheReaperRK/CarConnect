package cat.copernic.CarConnect.Repository.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioClient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentacioClientRepository extends MongoRepository<DocumentacioClient, String> {

    @Query(value = "{dni:  ?0}", count = true)
    Long countAllByDniQuery(String dni);

    // Método para buscar documentos por DNI de cliente
    DocumentacioClient findByDni(String dni);
}
