package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import cat.copernic.CarConnect.Repository.MySQL.ImatgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar las imágenes de vehículos. Este controlador
 * proporciona un endpoint para obtener las imágenes almacenadas en la base de
 * datos asociadas a los vehículos.
 *
 * <p>
 * El controlador interactúa con el repositorio {@link ImatgeRepository} para
 * acceder a las imágenes.</p>
 *
 * @author Carlos
 */
@RestController
public class ImatgeController {

    @Autowired
    private ImatgeRepository imagenRepository;  // Repositorio para acceder a las imágenes de vehículos

    /**
     * Obtiene una imagen de vehículo a partir de su ID.
     *
     * @param idImagen El ID de la imagen que se desea obtener.
     * @return La imagen en formato byte[] junto con los encabezados adecuados.
     * @throws Throwable Si ocurre algún error al obtener la imagen.
     */
    @GetMapping("/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@RequestParam Long idImagen) throws Throwable {
        // Buscar la imagen en el repositorio por su ID
        VehicleImages imagen = (VehicleImages) imagenRepository.findById(idImagen)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

        // Configurar los encabezados HTTP para la respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "image/jpeg"); // Tipo de contenido de la imagen (puede ser otro formato)

        // Retornar la imagen como respuesta con los encabezados adecuados
        return new ResponseEntity<>(imagen.getData(), headers, HttpStatus.OK);
    }
}
