package cat.copernic.CarConnect.Repository.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioIncidencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentacioIncidenciaRepository extends MongoRepository<DocumentacioIncidencia, String> {

    // Método para buscar documentos por ID de incidencia
    Long countAllById(String id);

    @Query(value = "{id:  ?0}", count = true)
    Long countAllByIdQuery(String id);

    DocumentacioIncidencia findByIdIncidencia(String idIncidencia);
}
