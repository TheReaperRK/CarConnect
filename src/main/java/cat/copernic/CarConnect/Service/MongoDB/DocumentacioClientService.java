package cat.copernic.CarConnect.Service.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioClient;
import cat.copernic.CarConnect.Repository.MongoDB.DocumentacioClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Servicio que gestiona las operaciones relacionadas con la documentación de
 * los clientes. Permite guardar o actualizar la documentación de un cliente y
 * obtener la documentación asociada a un DNI específico.
 */
@Service
public class DocumentacioClientService {

    @Autowired
    private DocumentacioClientRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Guarda o actualiza la documentación de un cliente.
     *
     * @param documentacioClient El objeto que representa la documentación del
     * cliente a guardar o actualizar.
     * @return El objeto de tipo `DocumentacioClient` que fue guardado o
     * actualizado en la base de datos.
     */
    public DocumentacioClient saveDocument(DocumentacioClient documentacioClient) {
        return repository.save(documentacioClient);
    }

    /**
     * Obtiene la documentación asociada a un cliente mediante su DNI.
     *
     * @param dni El DNI del cliente cuya documentación se desea obtener.
     * @return Un `Optional` que contiene la documentación del cliente si se
     * encuentra, de lo contrario, estará vacío.
     */
    public Optional<DocumentacioClient> getDocumentByDni(String dni) {
        return Optional.ofNullable(repository.findByDni(dni));
    }
}
