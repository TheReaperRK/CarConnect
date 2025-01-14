package cat.copernic.CarConnect.Service.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioIncidencia;
import cat.copernic.CarConnect.Repository.MongoDB.DocumentacioIncidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Servicio que gestiona las operaciones relacionadas con la documentación de
 * las incidencias. Permite guardar o actualizar la documentación de una
 * incidencia y obtener la documentación asociada a un ID de incidencia
 * específico.
 */
@Service
public class DocumentacioIncidenciaService {

    @Autowired
    private DocumentacioIncidenciaRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Guarda o actualiza la documentación de una incidencia.
     *
     * @param documentacioIncidencia El objeto que representa la documentación
     * de la incidencia a guardar o actualizar.
     * @return El objeto de tipo `DocumentacioIncidencia` que fue guardado o
     * actualizado en la base de datos.
     */
    public DocumentacioIncidencia saveDocument(DocumentacioIncidencia documentacioIncidencia) {
        return repository.save(documentacioIncidencia);
    }

    /**
     * Obtiene la documentación asociada a una incidencia mediante su ID.
     *
     * @param idIncidencia El ID de la incidencia cuya documentación se desea
     * obtener.
     * @return Un `Optional` que contiene la documentación de la incidencia si
     * se encuentra, de lo contrario, estará vacío.
     */
    public Optional<DocumentacioIncidencia> getDocumentByIdIncidencia(String idIncidencia) {
        return Optional.ofNullable(repository.findByIdIncidencia(idIncidencia));
    }
}
