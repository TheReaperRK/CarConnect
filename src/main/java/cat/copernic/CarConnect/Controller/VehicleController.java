package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusVehicle;
import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Service.MySQL.VehicleService;
import cat.copernic.CarConnect.Repository.MySQL.LocalitzacioRepository;
import cat.copernic.CarConnect.Repository.MySQL.VehicleRepository;
import cat.copernic.CarConnect.Service.MySQL.LocalitzacioService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para gestionar los vehículos. Este controlador maneja las
 * operaciones relacionadas con los vehículos, como la creación, actualización,
 * eliminación, activación, desactivación, y la carga de imágenes de vehículos.
  * @author Carlos
 */
@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private AgentRepository agentRepo;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LocalitzacioService localitzacioService;

    @Autowired
    private LocalitzacioRepository localitzacioRepository;

    /**
     * Muestra la lista de vehículos. Este método obtiene todos los vehículos y
     * los pasa a la vista correspondiente.
     *
     * @param model El modelo para agregar los atributos necesarios para la
     * vista.
     * @return La vista "vehicles-list" que muestra la lista de vehículos.
     */
    @GetMapping
    public String listVehicles(Model model,  Authentication authentication) {
        
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
            
            boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if(esAdmin){
               List<Vehicle> vehicles = vehicleService.consultarVehicles();
               model.addAttribute("vehicles", vehicles);
            }else{
               List<Vehicle> vehiclesPropis = vehicleService.consultarVehiclesPropis(authentication);
               model.addAttribute("vehicles", vehiclesPropis);                
            }
            
        }else{
            List<Vehicle> vehicles = vehicleService.consultarVehicles();
            model.addAttribute("vehicles", vehicles);
        }
                
             
       
        return "vehicles-list";
        
    }

    /**
     * Muestra el formulario para crear un nuevo vehículo. Este método carga los
     * tipos de vehículos y las localizaciones disponibles.
     *
     * @param model El modelo para agregar los atributos necesarios para la
     * vista.
     * @return La vista "vehicles-form" que contiene el formulario de creación
     * de un vehículo.
     */
    @GetMapping("/create")
    public String createVehicleForm(Model model, Authentication authentication) {
        
        
        model.addAttribute("vehicle", new Vehicle());
        model.addAttribute("tipusVehicles", TipusVehicle.values());
        
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        if(authentication != null){
            
        
            boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if(esAdmin){
                 model.addAttribute("localitzacions", localitzacioRepository.findAll());
            }else{
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                List<Agent> agents = agentRepo.findByEmail(username);
                Agent agent = new Agent();
                if(!agents.isEmpty()){
                    agent = agents.getFirst();
                }else agent = null;

                Localitzacio localitzacio = agent.getLocalitzacio();
                model.addAttribute("localitzacions", localitzacio);              
            }
        }
        //model.addAttribute("localitzacions", localitzacioRepository.findAll());
        model.addAttribute("isCreating", true);
        return "vehicles-form";
    }

    /**
     * Guarda un nuevo vehículo en la base de datos. Este método procesa la
     * imagen del vehículo, si está presente, y la asocia al vehículo antes de
     * guardarlo.
     *
     * @param vehicle El vehículo a guardar.
     * @param imagen La imagen del vehículo.
     * @param model El modelo para agregar los atributos necesarios en caso de
     * error.
     * @return Redirige a la lista de vehículos si la operación es exitosa, o
     * muestra un mensaje de error si ocurre algún problema.
     */
    
    @PostMapping("/create")
    public String saveVehicle(@ModelAttribute Vehicle vehicle,
            @RequestParam("imagen") MultipartFile imagen,
            Model model, Authentication authentication) {
        try {
            if (imagen.isEmpty()) {
                vehicleService.saveVehicle(vehicle);
                return "redirect:/vehicles";
            }

            if (imagen != null && !imagen.isEmpty()) {
                VehicleImages vehicleImage = new VehicleImages();
                vehicleImage.setData(imagen.getBytes());
                vehicleImage.setVehicle(vehicle);

                if (vehicle.getImatges() == null) {
                    vehicle.setImatges(new ArrayList<>());
                }
                vehicle.getImatges().add(vehicleImage);
            }

            vehicleService.saveVehicle(vehicle);
            return "redirect:/vehicles";
        } catch (DniDuplicadoException | IOException | IllegalArgumentException | DniIncorrecteException e) {
            vehicle.setMatricula(null);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("vehicle", vehicle); // Mantén el vehículo con los valores que el usuario ha completado
            model.addAttribute("tipusVehicles", TipusVehicle.values());
            
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);

            if(authentication != null){


                boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
                if(esAdmin){
                     model.addAttribute("localitzacions", localitzacioRepository.findAll());
                }else{
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String username = userDetails.getUsername();
                    List<Agent> agents = agentRepo.findByEmail(username);
                    Agent agent = new Agent();
                    if(!agents.isEmpty()){
                        agent = agents.getFirst();
                    }else agent = null;

                    Localitzacio localitzacio = agent.getLocalitzacio();
                    model.addAttribute("localitzacions", localitzacio);              
                }
            }
            
            model.addAttribute("isCreating", true);
            return "vehicles-form";  // Redirige de nuevo al formulario de creación

        }
    }

    // Obtener modelos por marca
    @GetMapping("/models/{marca}")
    @ResponseBody
    public List<String> getModelsByMarca(@PathVariable String marca) {
        // Normalizar la marca a minúsculas
        String normalizedMarca = marca.toLowerCase();

        // Mapa de marcas y modelos
        Map<String, List<String>> marcaModeloMap = new HashMap<>();

        // Marcas y modelos existentes
        marcaModeloMap.put("toyota", Arrays.asList("Corolla", "Camry", "RAV4", "Land Cruiser", "Proace"));
        marcaModeloMap.put("honda", Arrays.asList("Civic", "Accord", "CR-V", "Pilot", "Stepwagon"));
        marcaModeloMap.put("ford", Arrays.asList("Fiesta", "Focus", "Mustang", "Explorer", "Transit"));
        marcaModeloMap.put("bmw", Arrays.asList("Serie 3", "Serie 5", "X5", "X3", "Serie 2 Gran Tourer"));
        marcaModeloMap.put("nissan", Arrays.asList("Qashqai", "X-Trail", "Leaf", "Juke", "NV200"));
        marcaModeloMap.put("audi", Arrays.asList("A3", "A4", "Q7", "Q5", "Transporter"));
        marcaModeloMap.put("volkswagen", Arrays.asList("Golf", "Passat", "Tiguan", "Arteon", "Transporter"));
        marcaModeloMap.put("mercedes", Arrays.asList("A-Class", "B-Class", "GLE", "GLA", "Vito"));
        marcaModeloMap.put("peugeot", Arrays.asList("208", "308", "3008", "5008", "Partner"));
        marcaModeloMap.put("renault", Arrays.asList("Clio", "Megane", "Kadjar", "Captur", "Trafic"));
        marcaModeloMap.put("seat", Arrays.asList("Ibiza", "Leon", "Ateca", "Tarraco", "Alhambra"));
        marcaModeloMap.put("fiat", Arrays.asList("500", "Panda", "Doblo", "Fiorino"));
        marcaModeloMap.put("citroen", Arrays.asList("C3", "C4", "Berlingo", "Jumper"));
        marcaModeloMap.put("opel", Arrays.asList("Astra", "Insignia", "Mokka", "Combo"));

        // Retorna los modelos para la marca normalizada o una lista vacía si no se encuentra
        return marcaModeloMap.getOrDefault(normalizedMarca, new ArrayList<>());
    }


    /**
     * Muestra el formulario para editar un vehículo existente. Este método
     * carga los datos del vehículo y las opciones de tipos de vehículos y
     * localizaciones.
     *
     * @param matricula La matrícula del vehículo a editar.
     * @param model El modelo para agregar los atributos necesarios para la
     * vista.
     * @return La vista "vehicles-form" con los datos del vehículo a editar.
     */
    @GetMapping("/edit/{matricula}")
    public String editVehicleForm(@PathVariable String matricula, Model model, Authentication authentication) {
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
        if (vehicle == null) {
            model.addAttribute("errorMessage", "El vehículo no existe.");
            return "redirect:/vehicles?error=VehicleNotFound"; // Redirige a la lista de vehículos si no se encuentra el vehículo
        }
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("tipusVehicles", TipusVehicle.values());
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
            model.addAttribute("isAuthenticated", isAuthenticated);

            if(authentication != null){


                boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
                if(esAdmin){
                     model.addAttribute("localitzacions", localitzacioRepository.findAll());
                }else{
                    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                    String username = userDetails.getUsername();
                    List<Agent> agents = agentRepo.findByEmail(username);
                    Agent agent = new Agent();
                    if(!agents.isEmpty()){
                        agent = agents.getFirst();
                    }else agent = null;

                    Localitzacio localitzacio = agent.getLocalitzacio();
                    model.addAttribute("localitzacions", localitzacio);              
                }
            }
       
        model.addAttribute("isCreating", false);
        return "vehicles-form"; // Si el vehículo existe, muestra el formulario de edición
    }

    /**
     * Actualiza los datos de un vehículo existente. Este método procesa la
     * imagen si se proporciona y actualiza el vehículo en la base de datos.
     *
     * @param matricula La matrícula del vehículo a actualizar.
     * @param imagen La nueva imagen del vehículo (opcional).
     * @param vehicle Los nuevos datos del vehículo.
     * @return Redirige a la lista de vehículos si la operación es exitosa, o
     * muestra un mensaje de error si ocurre algún problema.
     */
    @PostMapping("/edit/{matricula}")
    public String updateVehicle(@PathVariable String matricula,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @ModelAttribute Vehicle vehicle) {
        try {
            vehicleService.updateVehicle(matricula, vehicle, imagen);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/vehicles?error=ImageProcessingError";
        }
        return "redirect:/vehicles";
    }

    /**
     * Elimina un vehículo. Este método elimina el vehículo identificado por su
     * matrícula de la base de datos.
     *
     * @param matricula La matrícula del vehículo a eliminar.
     * @return Redirige a la lista de vehículos después de eliminar el vehículo.
     */
    @GetMapping("/delete/{matricula}")
    public String deleteVehicle(@PathVariable String matricula) {
        vehicleService.esborrarVehicle(matricula);
        return "redirect:/vehicles";
    }

    /**
     * Activa un vehículo. Este método activa el vehículo identificado por su
     * matrícula.
     *
     * @param matricula La matrícula del vehículo a activar.
     * @return Redirige a la lista de vehículos después de activar el vehículo.
     */
    @GetMapping("/activate/{matricula}")
    public String activateVehicle(@PathVariable String matricula, RedirectAttributes redirectAttributes) {
        try {
            if (vehicleService.isActivable(matricula, redirectAttributes)){
            vehicleService.activarVehicle(matricula);
            redirectAttributes.addFlashAttribute("success", "El vehicle amb matrícula " + matricula + " s'ha activat correctament.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/vehicles";
    }

    /**
     * Desactiva un vehículo. Este método desactiva el vehículo identificado por
     * su matrícula.
     *
     * @param matricula La matrícula del vehículo a desactivar.
     * @return Redirige a la lista de vehículos después de desactivar el
     * vehículo.
     */
    @GetMapping("/deactivate/{matricula}")
    public String deactivateVehicle(@PathVariable String matricula, RedirectAttributes redirectAttributes) {
        try {
            if(vehicleService.isDesactivable(matricula, redirectAttributes)){
            vehicleService.desactivarVehicle(matricula);
            redirectAttributes.addFlashAttribute("success", "El vehicle amb matrícula " + matricula + " s'ha desactivat correctament.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/vehicles";
    }


    /**
     * Carga un vehículo específico y sus imágenes. Este método carga la imagen
     * principal del vehículo y las demás imágenes asociadas a él, y las
     * convierte a formato Base64 para mostrarlas en la vista.
     *
     * @param matricula La matrícula del vehículo a cargar.
     * @param model El modelo para agregar los atributos necesarios para la
     * vista.
     * @return La vista "selected-vehicle" que muestra los detalles del vehículo
     * seleccionado.
     */
    @GetMapping("/selected/{matricula}")
    public String CargarVehicle(@PathVariable String matricula, Model model, Authentication authentication) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula).orElse(null);
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(authentication != null){
            model.addAttribute("authorities", authentication.getAuthorities());
        }
        if (vehicle != null) {
            byte[] mainImageData = vehicleService.getSmallestImageForVehicle(matricula);
            String mainImageBase64 = mainImageData != null ? Base64.getEncoder().encodeToString(mainImageData) : null;

            List<byte[]> restImageDataList = vehicleService.getRestImagesForVehicle(matricula);
            List<String> restImageBase64List = restImageDataList.stream()
                    .map(image -> Base64.getEncoder().encodeToString(image))
                    .collect(Collectors.toList());

            Map<String, String> vehicleImages = new HashMap<>();
            if (mainImageBase64 != null) {
                vehicleImages.put(vehicle.getMatricula(), mainImageBase64);
            }

            model.addAttribute("vehicle", vehicle);
            model.addAttribute("vehicleImages", vehicleImages);
            model.addAttribute("restImageBase64List", restImageBase64List);
        }

        return "selected-vehicle";
    }
}
