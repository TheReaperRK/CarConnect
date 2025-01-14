package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Entity.MySQL.Enums.Nacionalitats;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusClient;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Service.MySQL.ClientService;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar las operaciones relacionadas con los clientes.
 * Este controlador maneja las peticiones HTTP para crear, editar, eliminar y
 * listar clientes, así como para filtrar clientes según diferentes criterios.
 *
 * <p>
 * Las operaciones implican la validación de los DNI y licencias, la gestión de
 * los clientes, y la interacción con el servicio {@link ClientService} para
 * realizar operaciones CRUD y filtrados.</p>
 *
 * @author David
 */
@Controller
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;  // Servicio para gestionar los clientes

    /**
     * Muestra la lista de todos los clientes en el sistema.
     *
     * @param model El modelo para pasar la lista de clientes a la vista.
     * @return La vista con la lista de todos los clientes.
     */
    @GetMapping
    public String listClients(Model model, Authentication authentication) {
        try {
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("clients", clientService.getAllClients());
            return "client-list"; // Página que muestra la lista de clientes
        } catch (Exception e) {
            // Registra el error para depuración
            e.printStackTrace();
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("errorMessage", "Ha ocurrido un error al cargar la lista de clientes.");
            return "error-page";  // Página personalizada de error
        }
    }

    /**
     * Muestra el formulario para crear un nuevo cliente.
     *
     * @param model El modelo para pasar los datos a la vista.
     * @return La vista con el formulario de creación de un nuevo cliente.
     */
    @GetMapping("/create")
    public String createClientForm(Model model, Authentication authentication) {
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("client", new Client());
        return "client-form"; // Página con el formulario de creación
    }

    /**
     * Guarda un nuevo cliente en el sistema después de validar los datos. Si
     * ocurre un error con el DNI o la licencia, se retorna al formulario con el
     * mensaje de error.
     *
     * @param client El objeto cliente con los datos enviados desde el
     * formulario.
     * @param model El modelo para pasar información adicional a la vista.
     * @param caducitatDni La fecha de caducidad del DNI proporcionada por el
     * usuario.
     * @param cadLlicencia La fecha de caducidad de la licencia de conducir
     * proporcionada por el usuario.
     * @return La vista del formulario con los datos o redirige al listado de
     * clientes si se guarda correctamente.
     */
    @PostMapping("/create")
    public String saveClient(@ModelAttribute Client client, Model model, Authentication authentication,@RequestParam("llicenciaConduc") String llicenciaConduc ,@RequestParam("caducDni") String caducitatDni, @RequestParam("cadLlicenciaCond") String cadLlicencia) {
        try{
            client.setCaducitatDni(LocalDate.parse(caducitatDni));
            client.setCaducitatLlicenciaConduccio(LocalDate.parse(cadLlicencia));
            String llicenciaCond = llicenciaConduc;
            client.setLlicenciaConduccio(llicenciaCond);

            // Instanciar el codificador de contraseñas BCrypt
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // Encriptar la contraseña
            String encryptedPassword = passwordEncoder.encode(client.getPassword());
            client.setPassword(encryptedPassword);
            
            clientService.saveClient(client);
            return "redirect:/clients";
        }catch(DniDuplicadoException | DniIncorrecteException e){
            String caducitatDniForm = caducitatDni;           
            String caducitatLlicenciaForm = cadLlicencia;
            
            client.setDni(null);
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("llicenciaCond", llicenciaConduc);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("fechaCaducitatDni", caducitatDni);
            model.addAttribute("fechaCaducitatLlicencia", cadLlicencia);
            model.addAttribute("errorMessage", e.getMessage());  // Mensaje de error
            return "client-form";  // Retorna al formulario con el error
        }
    }

    /**
     * Muestra el formulario para editar un cliente existente.
     *
     * @param dni El DNI del cliente que se desea editar.
     * @param model El modelo para pasar los datos a la vista.
     * @return La vista del formulario de edición de cliente.
     */
    @GetMapping("/edit/{dni}")
    public String editClientForm(@PathVariable String dni, Model model, Authentication authentication) {
         // Obtener el cliente (suponiendo que clientService.getClientByDni() devuelve un cliente con LocalDate)
        Client client = clientService.getClientByDni(dni);
        
        LocalDate caducitatDni= client.getCaducitatDni();
        String caducitatDniForm = caducitatDni.toString();
        
        LocalDate caducitatLlicencia= client.getCaducitatLlicenciaConduccio();
        String caducitatLlicenciaForm = caducitatLlicencia.toString();
        
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
            
        model.addAttribute("llicenciaCond", client.getLlicenciaConduccio());
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("client", client);
        model.addAttribute("fechaCaducitatDni", client.getCaducitatDni().toString());
        model.addAttribute("fechaCaducitatLlicencia", client.getCaducitatLlicenciaConduccio().toString());

        return "client-form";  // Vista del formulario de edición
    }

    /**
     * Guarda los cambios en un cliente existente. Si ocurre un error con el DNI
     * o la licencia, se retorna al formulario con el mensaje de error.
     *
     * @param dni El DNI del cliente que se desea actualizar.
     * @param client El objeto cliente con los nuevos datos.
     * @param model El modelo para pasar información adicional a la vista.
     * @param caducitatDni La nueva fecha de caducidad del DNI proporcionada por
     * el usuario.
     * @param cadLlicencia La nueva fecha de caducidad de la licencia de
     * conducir proporcionada por el usuario.
     * @return Redirige al listado de clientes o vuelve al formulario con el
     * error.
     */
    @PostMapping("/edit/{dni}")
    public String updateClient(@PathVariable String dni, @ModelAttribute Client client, Model model, Authentication authentication ,@RequestParam("llicenciaConduc") String llicenciaConduc, @RequestParam("caducDni") String caducitatDni, @RequestParam("cadLlicenciaCond") String cadLlicencia) {
        try {
            client.setCaducitatDni(LocalDate.parse(caducitatDni));  // Establece la nueva fecha de caducidad del DNI
            client.setCaducitatLlicenciaConduccio(LocalDate.parse(cadLlicencia));  // Establece la nueva fecha de caducidad de la licencia
            String llicenciaCond = llicenciaConduc;
            client.setLlicenciaConduccio(llicenciaCond);
            // Instanciar el codificador de contraseñas BCrypt
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // Encriptar la contraseña
            String encryptedPassword = passwordEncoder.encode(client.getPassword());
            client.setPassword(encryptedPassword);
            clientService.updateClient(dni, client);
            return "redirect:/clients";
        }catch(DniDuplicadoException | DniIncorrecteException e){
            String caducitatDniForm = caducitatDni;           
            String caducitatLlicenciaForm = cadLlicencia;
            
            client.setDni(dni);
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("llicenciaCond", llicenciaConduc);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("fechaCaducitatDni", caducitatDni);
            model.addAttribute("fechaCaducitatLlicencia", cadLlicencia);
            model.addAttribute("errorMessage", e.getMessage());  // Mensaje de error
            return "client-form";  // Retorna al formulario con el error
        }
    }

    /**
     * Elimina un cliente del sistema utilizando su DNI.
     *
     * @param dni El DNI del cliente que se desea eliminar.
     * @return Redirige al listado de clientes.
     */
    @GetMapping("/delete/{dni}")
    public String deleteClient(@PathVariable String dni) {
        clientService.deleteClient(dni);  // Elimina el cliente
        return "redirect:/clients";  // Redirige al listado de clientes
    }

    /**
     * Filtra los clientes según los criterios proporcionados.
     *
     * @param llicenciaConduccio El número de la licencia de conducir para
     * filtrar.
     * @param tipusClient El tipo de cliente para filtrar.
     * @param email El correo electrónico para filtrar.
     * @param model El modelo para pasar los clientes filtrados a la vista.
     * @return La vista con la lista de clientes filtrados.
     */
    @GetMapping("/filter")
    public String filterClients(
            @RequestParam Optional<String> llicenciaConduccio,
            @RequestParam Optional<String> tipusClient,
            @RequestParam Optional<String> nacionalitat,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> telefono,

            Model model, Authentication authentication) {

        String licencia = llicenciaConduccio.filter(val -> !val.isEmpty()).orElse(null);  // Filtra la licencia
        String mail = email.filter(val -> !val.isEmpty()).orElse(null);  // Filtra el email
        String telefon = telefono.filter(val -> !val.isEmpty()).orElse(null); //Filtra el telefon

        // Convierte el tipo de cliente a TipusClient si está presente
        Optional<TipusClient> tipusClientEnum = tipusClient.map(value -> {
            try {
                return TipusClient.valueOf(value);  // Convierte el valor a TipusClient
            } catch (IllegalArgumentException e) {
                return null;  // Si el valor no coincide, retorna null
            }
        });
        
        // Convierte la nacionalidad a Nacionalitat si está presente
        Optional<Nacionalitats> nacionalitatEnum = nacionalitat.map(value -> {
            try {
                return Nacionalitats.valueOf(value);  // Convierte el valor a Nacionalitat
            } catch (IllegalArgumentException e) {
                return null;  // Si el valor no coincide, retorna null
            }
        });

        // Llama al servicio para obtener los clientes filtrados
        List<Client> filteredClients = clientService.filterClients(licencia, tipusClientEnum, mail, nacionalitatEnum, telefon);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("clients", filteredClients);

        return "client-list";  // Vista de la lista de clientes filtrados
    }
}
