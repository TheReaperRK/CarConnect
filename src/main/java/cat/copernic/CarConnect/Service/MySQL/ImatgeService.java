package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import cat.copernic.CarConnect.Repository.MySQL.ImatgeRepository;
import cat.copernic.CarConnect.utils.ImatgeUtils;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona el almacenamiento de imágenes asociadas a vehículos.
 * Este servicio permite guardar imágenes como bytes en la base de datos.
 */
@Service
public class ImatgeService {

    @Autowired
    private ImatgeRepository imatgeRepository;

    /**
     * Guarda una imagen convertida a bytes en la base de datos. La imagen se
     * asocia a un vehículo específico.
     *
     * @param rutaImagen La ruta del archivo de la imagen a guardar.
     * @param vehiculo El vehículo al que se asociará la imagen.
     */
    public void guardarImagen(String rutaImagen, Vehicle vehiculo) {
        try {
            // Convierte la imagen a un arreglo de bytes
            byte[] imagenBytes = ImatgeUtils.convertirImagenABytes(rutaImagen);

            // Crea un objeto de imagen y asigna los datos
            VehicleImages imatge = new VehicleImages();
            imatge.setNombre("Ejemplo de Imagen"); // Nombre de la imagen, este puede personalizarse
            imatge.setData(imagenBytes);  // Los datos de la imagen en formato byte[]
            imatge.setVehicle(vehiculo);  // Se asocia al vehículo

            // Guarda la imagen en la base de datos
            imatgeRepository.save(imatge);
            System.out.println("Imagen guardada correctamente.");
        } catch (IOException e) {
            // Manejo de excepciones en caso de error al convertir o guardar la imagen
            System.err.println("Error al guardar la imagen: " + e.getMessage());
        }
    }
}
