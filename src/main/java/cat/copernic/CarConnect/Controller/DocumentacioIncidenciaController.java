package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.DocumentacioIncidencia;
import cat.copernic.CarConnect.Service.MongoDB.DocumentacioIncidenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controlador para gestionar las operaciones relacionadas con la documentación
 * de las incidencias. Este controlador maneja las peticiones HTTP para guardar
 * y obtener la documentación asociada a una incidencia.
 *
 * <p>
 * El controlador se comunica con el servicio
 * {@link DocumentacioIncidenciaService} para realizar operaciones CRUD sobre la
 * colección de documentación de incidencias.</p>
 *
 * @author David
 */
@RestController
@RequestMapping("/documentacio-incidencia")
public class DocumentacioIncidenciaController {

    @Autowired
    private DocumentacioIncidenciaService service;  // Servicio para gestionar la documentación de incidencias

    /**
     * Guarda la documentación asociada a una incidencia en la base de datos.
     *
     * @param documentacioIncidencia El objeto de tipo
     * {@link DocumentacioIncidencia} con la documentación a guardar.
     * @return El objeto de tipo {@link DocumentacioIncidencia} guardado.
     */
    @PostMapping
    public DocumentacioIncidencia saveDocument(@RequestBody DocumentacioIncidencia documentacioIncidencia) {
        return service.saveDocument(documentacioIncidencia);  // Llama al servicio para guardar la documentación de la incidencia
    }

    /**
     * Obtiene la documentación asociada a una incidencia a partir de su ID.
     *
     * @param idIncidencia El ID de la incidencia cuya documentación se desea
     * obtener.
     * @return Un objeto {@link Optional} que contiene la documentación de la
     * incidencia si existe, o vacío si no se encuentra.
     */
    @GetMapping("/{idIncidencia}")
    public Optional<DocumentacioIncidencia> getDocumentByIdIncidencia(@PathVariable String idIncidencia) {
        return service.getDocumentByIdIncidencia(idIncidencia);  // Llama al servicio para obtener la documentación de la incidencia
    }
}
