/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Service.MySQL.LocalitzacioService;
import cat.copernic.CarConnect.Service.MySQL.VehicleService;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador principal de la aplicación. Este controlador gestiona las
 * solicitudes a las páginas principales de la aplicación, como la página de
 * inicio, la página de login y la página del cliente. Además, maneja la carga
 * de vehículos y sus respectivas imágenes, junto con las localizaciones.
 *
 * @author Usuario
 */
@Controller
public class MainController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LocalitzacioService localitzacioService;

    /**
     * Método que maneja las solicitudes a la página de inicio ("/"). Recupera
     * la lista de vehículos y sus imágenes codificadas en base64, junto con las
     * localizaciones y los añade al modelo para su visualización en la vista.
     *
     * @param model El modelo donde se almacenan los atributos para la vista.
     * @return La vista "index" que se muestra al usuario en la página de
     * inicio.
     */
    @GetMapping("/")
        public String homePage(Model model, Authentication authentication) {
        List<Vehicle> vehicles = vehicleService.consultarVehicles();
        Map<String, String> vehicleImages = new HashMap<>();

        // Obtener las imágenes de los vehículos y convertirlas a base64
        for (Vehicle vehicle : vehicles) {
            byte[] imageData = vehicleService.getSmallestImageForVehicle(vehicle.getMatricula());
            if (imageData != null) {
                String base64Image = Base64.getEncoder().encodeToString(imageData);
                vehicleImages.put(vehicle.getMatricula(), base64Image);
            }
        }
         
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicleImages", vehicleImages); // Añadir imágenes codificadas
        model.addAttribute("localitzacions", localitzacioService.consultarLocalitzacions());
        return "index"; // Vista de la página de inicio
    }

    /**
     * Método que maneja las solicitudes a la página de login ("/login").
     * Redirige al formulario de login.
     *
     * @return La vista "login", que muestra el formulario de inicio de sesión.
     */
    @GetMapping("/login")
    public String redirigirLoging() {
        return "login"; // Vista del login
    }

    /**
     * Método que maneja las solicitudes a la página del cliente ("/client").
     * Renderiza la página correspondiente a la vista del cliente.
     *
     * @param model El modelo donde se pueden añadir datos relacionados al
     * cliente.
     * @return La vista "client", que muestra la página del cliente.
     */
    @GetMapping("/client")
    public String clientPage(Model model) {
        // Aquí puedes agregar los datos relacionados al cliente si es necesario
        return "client"; // Renderiza la página "client.html"
    }
}
