package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioClient;
import cat.copernic.CarConnect.Service.MongoDB.DocumentacioClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para gestionar las operaciones relacionadas con la documentación
 * de los clientes. Este controlador maneja las peticiones HTTP para guardar y
 * obtener la documentación de los clientes.
 *
 * <p>
 * El controlador se comunica con el servicio {@link DocumentacioClientService}
 * para realizar operaciones CRUD sobre la colección de documentación de
 * clientes.</p>
 *
 * @author David
 */
@RestController
@RequestMapping("/documentacio-client")
public class DocumentacioClientController {

    @Autowired
    private DocumentacioClientService service;  // Servicio para gestionar la documentación de los clientes

    /**
     * Guarda la documentación de un cliente en la base de datos.
     *
     * @param documentacioClient El objeto de tipo {@link DocumentacioClient}
     * con la documentación a guardar.
     * @return El objeto de tipo {@link DocumentacioClient} guardado.
     */
    @PostMapping
    public DocumentacioClient saveDocument(@RequestBody DocumentacioClient documentacioClient) {
        return service.saveDocument(documentacioClient);  // Llama al servicio para guardar la documentación
    }

    /**
     * Obtiene la documentación de un cliente a partir de su DNI.
     *
     * @param dni El DNI del cliente cuya documentación se desea obtener.
     * @return Un objeto {@link Optional} que contiene la documentación del
     * cliente si existe, o vacío si no se encuentra.
     */
    @GetMapping("/{dni}")
    public Optional<DocumentacioClient> getDocumentByDni(@PathVariable String dni) {
        return service.getDocumentByDni(dni);  // Llama al servicio para obtener la documentación del cliente
    }
}
