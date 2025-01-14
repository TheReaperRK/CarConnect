package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Service.MySQL.LocalitzacioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;

/**
 * Controlador que gestiona las solicitudes relacionadas con las localizaciones.
 * Permite crear, editar, eliminar y listar localizaciones, así como filtrarlas
 * por varios criterios.
 */
@Controller
@RequestMapping("/localitzacions")
public class LocalitzacioController {

    @Autowired
    private LocalitzacioService localitzacioService;
    
    
    /**
     * Muestra la lista de todas las localizaciones registradas.
     *
     * @param model El modelo de la vista.
     * @return El nombre de la vista que lista las localizaciones.
     */
    @GetMapping
    public String listLocalitzacions(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            
            boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if(esAdmin){
               List<Localitzacio> localitzacions = localitzacioService.consultarLocalitzacions();
               model.addAttribute("localitzacions", localitzacions);
            }else{
               List<Localitzacio> localitzacionsPropies = localitzacioService.consultarLocalitzacions();
               model.addAttribute("localitzacions", localitzacionsPropies);                
            }
            return "localitzacions-list";
        }else{
            List<Localitzacio> localitzacions = localitzacioService.consultarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            return "localitzacions-list";
        }
        
        

    }

    /**
     * Muestra el formulario para crear una nueva localización.
     *
     * @param model El modelo de la vista.
     * @return El nombre de la vista que muestra el formulario de creación.
     */
    @GetMapping("/create")
    public String createLocalitzacioForm(Model model) {
        // Cargar las comunidades y provincias
        String provinciasJson = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            provinciasJson = objectMapper.writeValueAsString(COMUNITATS_I_PROVINCIES);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Si ocurre un error, lo capturamos
            model.addAttribute("error", "Error al cargar las provincias.");
            return "localitzacions-form"; // Puedes retornar la página con el error
        }

        model.addAttribute("localitzacio", new Localitzacio());
        model.addAttribute("comunitats", COMUNITATS_I_PROVINCIES.keySet());
        model.addAttribute("comunitatsIProvincias", provinciasJson);

        return "localitzacions-form";
    }

    /**
     * Guarda la nueva localización tras la validación de los datos del
     * formulario.
     *
     * @param localitzacio La localización a guardar.
     * @param model El modelo de la vista.
     * @return La vista de redirección o el formulario con errores.
     */
    @PostMapping("/create")
    public String saveLocalitzacio(@ModelAttribute Localitzacio localitzacio, Model model) {
        // Validar el Codi Postal
        if (!localitzacio.getCodiPostal().matches("\\d{5}")) {
            model.addAttribute("error", "El codi postal ha de ser un número de 5 dígits.");
            // Recargar las comunidades y provincias
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                model.addAttribute("comunitatsIProvincias", objectMapper.writeValueAsString(COMUNITATS_I_PROVINCIES));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Si ocurre un error, lo capturamos
                model.addAttribute("error", "Error al cargar las provincias.");
            }
            model.addAttribute("comunitats", COMUNITATS_I_PROVINCIES.keySet());
            localitzacio.setCodiPostal(null); // Eliminar el codiPostal para asegurarse de que el formulario no se trata como edición
            return "localitzacions-form"; // Se mantiene en la página de creación
        }

        // Validar el número del carrer
        if (localitzacio.getNum() < 0) {
            model.addAttribute("error", "El número del carrer no pot ser negatiu.");
            // Recargar las comunidades y provincias
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                model.addAttribute("comunitatsIProvincias", objectMapper.writeValueAsString(COMUNITATS_I_PROVINCIES));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // Si ocurre un error, lo capturamos
                model.addAttribute("error", "Error al cargar las provincias.");
            }
            model.addAttribute("comunitats", COMUNITATS_I_PROVINCIES.keySet());
            localitzacio.setCodiPostal(null); // Eliminar el codiPostal
            return "localitzacions-form"; // Se mantiene en la página de creación
        }

        // Si todo es válido, crear la localización
        try {
            localitzacioService.crearLocalitzacio(localitzacio);
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear la localització.");
            // Recargar las comunidades y provincias
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                model.addAttribute("comunitatsIProvincias", objectMapper.writeValueAsString(COMUNITATS_I_PROVINCIES));
            } catch (JsonProcessingException ex) {
                ex.printStackTrace(); // Si ocurre un error, lo capturamos
                model.addAttribute("error", "Error al cargar las provincias.");
            }
            model.addAttribute("comunitats", COMUNITATS_I_PROVINCIES.keySet());
            localitzacio.setCodiPostal(null); // Eliminar el codiPostal
            return "localitzacions-form"; // Se mantiene en la página de creación
        }

        return "redirect:/localitzacions"; // Redirige a la lista de localizaciones si todo es correcto
    }

    /**
     * Muestra el formulario para editar una localización existente.
     *
     * @param codiPostal El código postal de la localización a editar.
     * @param model El modelo de la vista.
     * @return El nombre de la vista que muestra el formulario de edición.
     */
    @GetMapping("/edit/{codiPostal}")
    public String editLocalitzacioForm(@PathVariable String codiPostal, Model model) {
        Localitzacio localitzacio = localitzacioService.consultarLocalitzacions().stream()
                .filter(l -> l.getCodiPostal().equals(codiPostal))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Localització no trobada."));

        String comunidadSeleccionada = localitzacio.getComunitatAutonoma();
        String provinciaSeleccionada = localitzacio.getProvincia();

        // Cargar las comunidades y provincias
        String provinciasJson = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            provinciasJson = objectMapper.writeValueAsString(COMUNITATS_I_PROVINCIES);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Si ocurre un error, lo capturamos
            model.addAttribute("error", "Error al cargar las provincias.");
            return "localitzacions-form"; // Puedes retornar la página con el error
        }

        model.addAttribute("localitzacio", localitzacio);
        model.addAttribute("comunitats", COMUNITATS_I_PROVINCIES.keySet());
        model.addAttribute("comunitatsIProvincias", provinciasJson);
        model.addAttribute("comunidadSeleccionada", comunidadSeleccionada);
        model.addAttribute("provinciaSeleccionada", provinciaSeleccionada);

        return "localitzacions-form";
    }

    /**
     * Actualiza la localización después de la validación de los datos del
     * formulario.
     *
     * @param codiPostal El código postal de la localización a actualizar.
     * @param localitzacio Los datos actualizados de la localización.
     * @param model El modelo de la vista.
     * @return La vista de redirección o el formulario con errores.
     */
    @PostMapping("/edit/{codiPostal}")
    public String updateLocalitzacio(@PathVariable String codiPostal, @ModelAttribute Localitzacio localitzacio, Model model) {
        // Validación del Codi Postal
        if (!localitzacio.getCodiPostal().matches("\\d{5}")) {
            model.addAttribute("error", "El codi postal ha de ser un número de 5 dígits.");
            return "localitzacions-form";
        }

        // Verificar si el nuevo código postal ya existe (exceptuando el caso de la misma localización)
        if (!codiPostal.equals(localitzacio.getCodiPostal()) && localitzacioService.consultarLocalitzacions().stream()
                .anyMatch(l -> l.getCodiPostal().equals(localitzacio.getCodiPostal()))) {
            model.addAttribute("error", "Ja existeix una localització amb aquest codi postal.");
            return "localitzacions-form";
        }

        // Validación del número del carrer
        if (localitzacio.getNum() < 0) {
            model.addAttribute("error", "El número del carrer no pot ser negatiu.");
            return "localitzacions-form";
        }

        localitzacioService.modificarLocalitzacio(codiPostal, localitzacio);
        return "redirect:/localitzacions";
    }

    /**
     * Elimina una localización según el código postal.
     *
     * @param codiPostal El código postal de la localización a eliminar.
     * @return La vista de redirección a la lista de localizaciones.
     */
    @GetMapping("/delete/{codiPostal}")
    public String deleteLocalitzacio(@PathVariable String codiPostal) {
        localitzacioService.eliminarLocalitzacio(codiPostal);
        return "redirect:/localitzacions";
    }

    /**
     * Filtra las localizaciones según los parámetros proporcionados.
     *
     * @param codiPostal El código postal de la localización a filtrar.
     * @param comunitatAutonoma La comunidad autónoma a filtrar.
     * @param provincia La provincia a filtrar.
     * @param ciutat La ciudad a filtrar.
     * @param model El modelo de la vista.
     * @return La vista que muestra la lista de localizaciones filtradas.
     */
    @GetMapping("/filter")
    public String filterLocalitzacions(@RequestParam(required = false) String codiPostal,
            @RequestParam(required = false) String comunitatAutonoma,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String ciutat,
            Model model) {
        List<Localitzacio> localitzacions = localitzacioService.filtrarLocalitzacio(codiPostal, comunitatAutonoma, provincia, ciutat);
        model.addAttribute("localitzacions", localitzacions);
        return "localitzacions-list"; // Nombre de la vista
    }

    /**
     * Mapa estático que contiene las comunidades autónomas y sus provincias
     * asociadas.
     */
    private static final Map<String, List<String>> COMUNITATS_I_PROVINCIES = new HashMap<>();

    static {
        // Definición de comunidades autónomas y sus provincias
        COMUNITATS_I_PROVINCIES.put("Andalucía", Arrays.asList("Almería", "Cádiz", "Córdoba", "Granada", "Huelva", "Jaén", "Málaga", "Sevilla"));
        COMUNITATS_I_PROVINCIES.put("Aragón", Arrays.asList("Huesca", "Teruel", "Zaragoza"));
        COMUNITATS_I_PROVINCIES.put("Asturias", Arrays.asList("Asturias"));
        COMUNITATS_I_PROVINCIES.put("Illes Balears", Arrays.asList("Palma", "Ibiza", "Menorca"));
        COMUNITATS_I_PROVINCIES.put("Canarias", Arrays.asList("Las Palmas", "Santa Cruz de Tenerife"));
        COMUNITATS_I_PROVINCIES.put("Cantabria", Arrays.asList("Cantabria"));
        COMUNITATS_I_PROVINCIES.put("Castilla y León", Arrays.asList("Ávila", "Burgos", "León", "Palencia", "Salamanca", "Segovia", "Soria", "Valladolid", "Zamora"));
        COMUNITATS_I_PROVINCIES.put("Castilla-La Mancha", Arrays.asList("Albacete", "Ciudad Real", "Cuenca", "Guadalajara", "Toledo"));
        COMUNITATS_I_PROVINCIES.put("Cataluña", Arrays.asList("Barcelona", "Girona", "Lleida", "Tarragona"));
        COMUNITATS_I_PROVINCIES.put("Extremadura", Arrays.asList("Badajoz", "Cáceres"));
        COMUNITATS_I_PROVINCIES.put("Galicia", Arrays.asList("A Coruña", "Lugo", "Ourense", "Pontevedra"));
        COMUNITATS_I_PROVINCIES.put("Madrid", Arrays.asList("Madrid"));
        COMUNITATS_I_PROVINCIES.put("Murcia", Arrays.asList("Murcia"));
        COMUNITATS_I_PROVINCIES.put("Navarra", Arrays.asList("Navarra"));
        COMUNITATS_I_PROVINCIES.put("La Rioja", Arrays.asList("La Rioja"));
        COMUNITATS_I_PROVINCIES.put("País Vasco", Arrays.asList("Álava", "Gipuzkoa", "Bizkaia"));
        COMUNITATS_I_PROVINCIES.put("Comunidad Valenciana", Arrays.asList("Alicante", "Castellón", "Valencia"));
        COMUNITATS_I_PROVINCIES.put("Ceuta", Arrays.asList("Ceuta"));
        COMUNITATS_I_PROVINCIES.put("Melilla", Arrays.asList("Melilla"));
    }
}
