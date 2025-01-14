/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Service.MySQL.ClientService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador encargado del registro de nuevos clientes. Este controlador
 * maneja la visualización del formulario de registro, la validación y el
 * procesamiento de los datos enviados por el usuario, y la creación del nuevo
 * cliente en la base de datos.
 *
 * @author David
 */
@Controller
@RequestMapping("/Registre")
public class RegistreController {

    @Autowired
    private ClientService clientService;

    /**
     * Método que maneja las solicitudes a la página de registro (/Registre).
     * Muestra el formulario de registro donde el usuario puede ingresar sus
     * datos.
     *
     * @param model El modelo donde se almacenan los atributos para la vista.
     * @return La vista "Registre", que contiene el formulario de registro.
     */
    @GetMapping
    public String RegistreForm(Model model) {
        model.addAttribute("client", new Client()); // Añadir un nuevo objeto Client al modelo
        return "Registre"; // Vista que muestra el formulario de registro
    }

    /**
     * Método que maneja el procesamiento de los datos del formulario de
     * registro. Recibe los datos enviados por el formulario, incluyendo las
     * fechas de caducidad del DNI y la licencia de conducir, y encripta la
     * contraseña antes de guardar el nuevo cliente en la base de datos.
     *
     * @param client El cliente con los datos ingresados en el formulario.
     * @param caducitatDni La fecha de caducidad del DNI en formato String.
     * @param cadLlicencia La fecha de caducidad de la licencia de conducir en
     * formato String.
     * @return Redirige a la página de inicio "index" después de guardar el
     * cliente.
     */
    @PostMapping("/create")
    public String saveClient(@ModelAttribute Client client, @RequestParam("caducDni") String caducitatDni, @RequestParam("cadLlicenciaCond") String cadLlicencia) {
        // Parsear las fechas de caducidad del DNI y la licencia de conducir
        client.setCaducitatDni(LocalDate.parse(caducitatDni));
        client.setCaducitatLlicenciaConduccio(LocalDate.parse(cadLlicencia));

        // Instanciar el codificador de contraseñas BCrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Encriptar la contraseña
        String encryptedPassword = passwordEncoder.encode(client.getPassword());
        client.setPassword(encryptedPassword);

        // Guardar el cliente en la base de datos
        clientService.saveClient(client);
        return "/login";
    }
}
