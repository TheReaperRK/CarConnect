package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Repository.MySQL.LocalitzacioRepository;
import cat.copernic.CarConnect.Service.MySQL.AgentService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar las operaciones relacionadas con los agentes. Este
 * controlador maneja las peticiones HTTP para crear, editar, eliminar y listar
 * agentes, así como para filtrar agentes por su DNI.
 *
 * <p>
 * Las operaciones implican la validación de los DNI, la gestión de las
 * localizaciones, y la interacción con el servicio {@link AgentService} para
 * realizar operaciones CRUD.</p>
 *
 * @author David
 */
@Controller
@RequestMapping("/agents")
public class AgentController {

    @Autowired
    private AgentService agentService;  // Servicio para gestionar los agentes

    @Autowired
    private LocalitzacioRepository localitzacioRepository;  // Repositorio de localizaciones

    /**
     * Muestra el formulario para crear un nuevo agente. Este método carga el
     * formulario y prepara los datos necesarios para la creación del agente.
     *
     * @param model El modelo que se pasa a la vista para renderizar los datos.
     * @return La vista del formulario de creación de agentes.
     */
    @GetMapping("/create")
    public String crearAgentForm(Model model, Authentication authentication){
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("agent", new Agent());
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        List<Localitzacio> localitzacions = localitzacioRepository.findAll();
        List<Localitzacio> filteredLocalitzacions = localitzacions.stream()
                .filter(localitzacio -> localitzacio.getAgent() == null)
                .collect(Collectors.toList());

        model.addAttribute("localitzacions", filteredLocalitzacions);  // Lista de localizaciones
        return "agent-form";  // Vista del formulario
    }

    /**
     * Guarda un nuevo agente en el sistema después de validar los datos. Si
     * ocurre un error de DNI duplicado o incorrecto, se devuelve el formulario
     * con un mensaje de error.
     *
     * @param agent El objeto agente con los datos enviados desde el formulario.
     * @param model El modelo para pasar información adicional a la vista.
     * @param caducitatDni La fecha de caducidad del DNI proporcionada por el
     * usuario.
     * @param cadLlicencia La fecha de caducidad de la licencia de conducir
     * proporcionada por el usuario.
     * @param dataContact La fecha de contrato del agente proporcionada por el
     * usuario.
     * @return La vista del formulario de creación con los datos o redirige al
     * listado de agentes si se guarda correctamente.
     */
    @PostMapping("/create")
    public String guardarAgent(@ModelAttribute Agent agent, Model model, Authentication authentication, @RequestParam("llicenciaConduc") String llicenciaConduc, @RequestParam("caducDni") String caducitatDni, @RequestParam("cadLlicenciaCond") String cadLlicencia, @RequestParam("dataDeContact") String dataContact){
            
        try {
            // Establece las fechas de caducidad y contrato
            agent.setCaducitatDni(LocalDate.parse(caducitatDni));
            agent.setCaducitatLlicenciaConduccio(LocalDate.parse(cadLlicencia));
            agent.setDataContracte(LocalDate.parse(dataContact));
            String llicenciaCond = llicenciaConduc;
            agent.setLlicenciaConduccio(llicenciaCond);
            // Instanciar el codificador de contraseñas BCrypt
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // Encriptar la contraseña
            String encryptedPassword = passwordEncoder.encode(agent.getPassword());
            agent.setPassword(encryptedPassword);
            // Llama al servicio para crear el agente
            agentService.crearAgent(agent);
            
            return "redirect:/agents";
        }catch(DniDuplicadoException | DniIncorrecteException e){
               
            String dataContracteForm = dataContact;
            String caducitatDniForm = caducitatDni;           
            String caducitatLlicenciaForm = cadLlicencia;
             // Obtenir el agent (suponent que AgentService.getAgentByDni() retorna un agent amb LocalDate)
            agent.setDni(null);
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("llicenciaCond", llicenciaConduc);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("fechaDataContr", dataContact);
            model.addAttribute("fechaCaducitatDni", caducitatDni);
            model.addAttribute("fechaCaducitatLlicencia", cadLlicencia);

            // Filtra las localizaciones sin agentes asignados
            List<Localitzacio> localitzacions = localitzacioRepository.findAll();
            List<Localitzacio> filteredLocalitzacions = localitzacions.stream()
                    .filter(localitzacio -> localitzacio.getAgent() == null)
                    .collect(Collectors.toList());

            // Si el agente tiene una localización, la agrega a la lista
            Localitzacio localitzacio = agent.getLocalitzacio();
            if (localitzacio != null) {
                filteredLocalitzacions.add(0, localitzacio);
            }

            model.addAttribute("localitzacions", filteredLocalitzacions);  // Lista de localizaciones
            model.addAttribute("errorMessage", e.getMessage());  // Mensaje de error
            return "agent-form";  // Retorna al formulario con el error
        }
    }

    /**
     * Lista todos los agentes en el sistema.
     *
     * @param model El modelo para pasar los datos a la vista.
     * @return La vista que muestra la lista de agentes.
     */
    @GetMapping
    public String llistarAgent(Model model, Authentication authentication){
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("agents", agentService.llistarAgents());
        return "agent-list"; 
    }

    /**
     * Muestra el formulario para editar un agente existente.
     *
     * @param dni El DNI del agente que se desea editar.
     * @param model El modelo para pasar los datos a la vista.
     * @return La vista del formulario de edición de agentes.
     */
    @GetMapping("/edit/{dni}")
    public String editarAgentForm(@PathVariable String dni, Model model, Authentication authentication) {
        Agent agent = agentService.obtenirAgentPerDni(dni);
        LocalDate dataContracte= agent.getDataContracte();
        String dataContracteForm = dataContracte.toString();
        
        LocalDate caducitatDni= agent.getCaducitatDni();
        String caducitatDniForm = caducitatDni.toString();
        
        LocalDate caducitatLlicencia= agent.getCaducitatLlicenciaConduccio();
        String caducitatLlicenciaForm = caducitatLlicencia.toString();
         // Obtenir el agent (suponent que AgentService.getAgentByDni() retorna un agent amb LocalDate)
         
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("llicenciaCond", agent.getLlicenciaConduccio());
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("agent", agent);
        model.addAttribute("fechaDataContr", agent.getDataContracte().toString());
        model.addAttribute("fechaCaducitatDni", agent.getCaducitatDni().toString());
        model.addAttribute("fechaCaducitatLlicencia", agent.getCaducitatLlicenciaConduccio().toString());

        // Filtra las localizaciones sin agentes asignados
        List<Localitzacio> localitzacions = localitzacioRepository.findAll();
        List<Localitzacio> filteredLocalitzacions = localitzacions.stream()
                .filter(localitzacio -> localitzacio.getAgent() == null)
                .collect(Collectors.toList());

        Localitzacio localitzacio = agent.getLocalitzacio();
        if (localitzacio != null) {
            filteredLocalitzacions.add(0, localitzacio);  // Agrega la localización actual del agente
        }
        model.addAttribute("localitzacions", filteredLocalitzacions);  // Lista de localizaciones
        return "agent-form";  // Vista del formulario de edición
    }

    /**
     * Actualiza los datos de un agente existente. Si ocurre un error con el
     * DNI, se devuelve el formulario con el mensaje de error.
     *
     * @param dni El DNI del agente que se desea actualizar.
     * @param agentActualitzat El agente con los nuevos datos.
     * @param model El modelo para pasar los datos a la vista.
     * @param caducitatDni La nueva fecha de caducidad del DNI.
     * @param cadLlicencia La nueva fecha de caducidad de la licencia de
     * conducir.
     * @param dataContact La nueva fecha de contrato del agente.
     * @return La vista del listado de agentes o el formulario con el error.
     */
    @PostMapping("/edit/{dni}")
    public String actualitzarAgent(@PathVariable String dni, @ModelAttribute Agent agentActualitzat, Model model, Authentication authentication, @RequestParam("llicenciaConduc") String llicenciaConduc, @RequestParam("caducDni") String caducitatDni, @RequestParam("cadLlicenciaCond") String cadLlicencia, @RequestParam("dataDeContact") String dataContact) {
        try {
        agentActualitzat.setCaducitatDni(LocalDate.parse(caducitatDni));
        agentActualitzat.setCaducitatLlicenciaConduccio(LocalDate.parse(cadLlicencia));
        agentActualitzat.setDataContracte(LocalDate.parse(dataContact));
        String llicenciaCond = llicenciaConduc;
        agentActualitzat.setLlicenciaConduccio(llicenciaCond);
        // Instanciar el codificador de contraseñas BCrypt
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Encriptar la contraseña
        String encryptedPassword = passwordEncoder.encode(agentActualitzat.getPassword());
        agentActualitzat.setPassword(encryptedPassword);
        agentService.actualitzarAgent(dni, agentActualitzat);
        return "redirect:/agents";
        }catch(DniDuplicadoException | DniIncorrecteException e){
            String dataContracteForm = dataContact;
            String caducitatDniForm = caducitatDni;           
            String caducitatLlicenciaForm = cadLlicencia;
             // Obtenir el agent (suponent que AgentService.getAgentByDni() retorna un agent amb LocalDate)
            agentActualitzat.setDni(dni);
             
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);
            if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            }
            model.addAttribute("llicenciaCond", llicenciaConduc);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("fechaDataContr", dataContact);
            model.addAttribute("fechaCaducitatDni", caducitatDni);
            model.addAttribute("fechaCaducitatLlicencia", cadLlicencia);

            List<Localitzacio> localitzacions = localitzacioRepository.findAll();
            List<Localitzacio> filteredLocalitzacions = localitzacions.stream()
                    .filter(localitzacio -> localitzacio.getAgent() == null)
                    .collect(Collectors.toList());

            Localitzacio localitzacio = agentActualitzat.getLocalitzacio();
            if (localitzacio != null) {
                filteredLocalitzacions.add(0, localitzacio);
            }

            model.addAttribute("localitzacions", filteredLocalitzacions);  // Lista de localizaciones
            model.addAttribute("errorMessage", e.getMessage());  // Mensaje de error
            return "agent-form";  // Retorna al formulario con el error
        }
    }

    /**
     * Elimina un agente del sistema utilizando su DNI.
     *
     * @param dni El DNI del agente que se desea eliminar.
     * @param model El modelo para redirigir a la vista de la lista de agentes.
     * @return Redirige a la vista de la lista de agentes.
     */
    @GetMapping("/delete/{dni}")
    public String eliminarAgent(@PathVariable String dni, Model model) {
        agentService.eliminarAgent(dni);  // Elimina el agente
        return "redirect:/agents";  // Redirige al listado de agentes
    }

    /**
     * Filtra los agentes según el DNI proporcionado.
     *
     * @param dni El DNI por el cual filtrar los agentes. Si no se proporciona,
     * se listan todos los agentes.
     * @param model El modelo para pasar los datos filtrados a la vista.
     * @return La vista con la lista de agentes filtrados.
     */
    @GetMapping("/filter")
    public String filterAgents(
            @RequestParam Optional<String> dni,
            Model model, Authentication authentication) {

        // Filtra los agentes por el DNI
        List<Agent> filteredAgents = agentService.filterAgents(dni.orElse(null));
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        model.addAttribute("agents", filteredAgents);

        return "agent-list";  // Vista de la lista de agentes filtrados
    }
}
