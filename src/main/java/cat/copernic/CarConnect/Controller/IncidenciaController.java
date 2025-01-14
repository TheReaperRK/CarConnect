package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricIncidencies;
import cat.copernic.CarConnect.Entity.MySQL.Incidencia;
import cat.copernic.CarConnect.Entity.MySQL.IncidenciaFiles;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Repository.MySQL.IncidenciaRepository;
import cat.copernic.CarConnect.Service.MongoDB.HistoricIncidenciesService;
import cat.copernic.CarConnect.Service.MySQL.IncidenciaFilesService;
import cat.copernic.CarConnect.Service.MySQL.IncidenciaService;
import cat.copernic.CarConnect.Service.MySQL.VehicleService;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para gestionar las incidencias de vehículos. Este controlador
 * permite crear, editar, eliminar, y listar incidencias asociadas a vehículos,
 * así como gestionar los filtros para la visualización de incidencias.
 *
 * <p>
 * El controlador interactúa con los servicios {@link IncidenciaService} y
 * {@link VehicleService} para acceder a las incidencias y vehículos.</p>
 *
 * @author Toni
 */
@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    @Autowired
    private IncidenciaFilesService incidenciaFilesService;

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private HistoricIncidenciesService historicIncidenciesService;

    /**
     * Muestra el formulario para crear una nueva incidencia para un vehículo.
     *
     * @param matricula La matrícula del vehículo al que se asignará la
     * incidencia.
     * @param model El modelo para pasar la nueva incidencia al formulario.
     * @param redirectAttributes Atributos para redireccionar con mensajes de
     * error o éxito.
     * @return La vista del formulario para crear la incidencia.
     */
    @GetMapping("/create/{matricula}")
    public String createIncidenciaForm(@PathVariable String matricula, Model model, RedirectAttributes redirectAttributes) {
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);

        if (vehicle == null) {
            redirectAttributes.addFlashAttribute("error", "El vehículo no existe.");
            return "redirect:/vehicles";
        }

        if (incidenciaService.vehicleHasOpenIncidencia(matricula)) {
            redirectAttributes.addFlashAttribute("error", "No se pueden tener más de una incidencia abierta por vehículo.");
            return "redirect:/vehicles";
        }

        Incidencia nuevaIncidencia = new Incidencia();
        nuevaIncidencia.setVehicle(vehicle);
        nuevaIncidencia.setFecha(LocalDate.now());
        nuevaIncidencia.setOberta(true);

        model.addAttribute("incidencia", nuevaIncidencia);
        return "incidencias-form";
    }

    /**
     * Crea o actualiza una incidencia en la base de datos.
     *
     * @param incidencia La incidencia que se va a crear o actualizar.
     * @param result El resultado de la validación del formulario.
     * @param redirectAttributes Atributos para redireccionar con mensajes de
     * error o éxito.
     * @return La vista de redirección después de la operación.
     */
    
    private void saveIncidenciaToHistoric(Incidencia incidencia) {
        HistoricIncidencies historic = new HistoricIncidencies();
        historic.setDescription(incidencia.getDescription());
        historic.setCost(incidencia.getCost());
        historic.setVehicleId(incidencia.getVehicle().getMatricula());
        historic.setAberta(incidencia.isOberta());
        historic.setFecha(incidencia.getFecha());

        historicIncidenciesService.saveToHistoric(historic);
    }
    
    // Mètode creat incidència

    @PostMapping("/create/{matricula}")
    public String createOrUpdateIncidencia(@PathVariable String matricula, @RequestParam(value = "imagen", required = false) MultipartFile imagen, @Valid @ModelAttribute Incidencia incidencia, BindingResult result, RedirectAttributes redirectAttributes) throws IOException {
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);

        if (result.hasErrors()) {
            return "incidencias-form";
        }

        try {

            if (incidencia.getId() == null) {
                if (incidenciaService.vehicleHasOpenIncidencia(incidencia.getVehicle().getMatricula())) {
                    throw new IllegalStateException("No se pueden tener más de una incidencia abierta por vehículo.");
                }
                incidenciaService.saveIncidencia(incidencia);
                saveIncidenciaToHistoric(incidencia); // Desar a l'històric
                if (vehicleService.isDesactivable(matricula, redirectAttributes)){
                    vehicle.setActiu(false);
                    vehicleService.updateVehicle(matricula, vehicle, imagen);
                } else {
                        redirectAttributes.addFlashAttribute("error", "No es pot desactivar el vehicle perquè tè una reserva");
                    }
                redirectAttributes.addFlashAttribute("success", "Incidencia creada correctamente.");
            } else {
                incidenciaService.updateIncidencia(incidencia);
                saveIncidenciaToHistoric(incidencia); // Actualitzar a l'històric
                redirectAttributes.addFlashAttribute("success", "Incidencia actualizada correctamente.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/incidencias/list";
    }

    /**
     * Muestra el formulario para editar una incidencia existente.
     *
     * @param id El ID de la incidencia a editar.
     * @param model El modelo para pasar la incidencia existente al formulario.
     * @param redirectAttributes Atributos para redireccionar con mensajes de
     * error o éxito.
     * @return La vista del formulario de edición de la incidencia.
     */
    @GetMapping("/edit/{id}")
    public String editIncidenciaForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Incidencia> incidencia = incidenciaService.getIncidenciaById(id);

        if (incidencia.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La incidencia no existe.");
            return "redirect:/incidencias/list";
        }

        model.addAttribute("incidencia", incidencia.get());
        return "incidencias-form";
    }

    /**
     * Actualiza una incidencia existente en la base de datos.
     *
     * @param incidencia La incidencia actualizada que se va a guardar.
     * @param redirectAttributes Atributos para redireccionar con mensajes de
     * error o éxito.
     * @return La vista de redirección después de la operación.
     */
    public void updateIncidencia(Incidencia incidencia) {
        if (incidencia.getId() == null) {
            throw new IllegalArgumentException("No se puede actualizar una incidencia sin un ID.");
        }

        Optional<Incidencia> existingIncidencia = incidenciaRepository.findById(incidencia.getId());
        if (existingIncidencia.isPresent()) {
            Incidencia updatedIncidencia = existingIncidencia.get();

            // Actualizar solo los campos necesarios
            updatedIncidencia.setDescription(incidencia.getDescription());
            updatedIncidencia.setCost(incidencia.getCost());
            updatedIncidencia.setOberta(incidencia.isOberta()); // Actualizar estado

            // Si se cambia el vehículo asociado, actualizar la relación
            if (incidencia.getVehicle() != null && incidencia.getVehicle().getMatricula() != null) {
                Vehicle vehicle = vehicleService.getVehicleByMatricula(incidencia.getVehicle().getMatricula());
                updatedIncidencia.setVehicle(vehicle);
            }

            incidenciaRepository.save(updatedIncidencia);
        } else {
            throw new IllegalArgumentException("La incidencia con ID " + incidencia.getId() + " no existe.");
        }
    }

    /**
     * Desactiva una incidencia, cambiando su estado de abierto a cerrado.
     *
     * @param id El ID de la incidencia que se desea desactivar.
     * @param redirectAttributes Atributos para redireccionar con mensajes de
     * error o éxito.
     * @return La vista de redirección después de la operación.
     */
    @GetMapping("/deactivate/{id}")
    public String deactivateIncidencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidenciaService.toggleIncidenciaState(id);
            Incidencia incidencia = incidenciaService.getIncidenciaById(id).orElseThrow(() -> new IllegalArgumentException("La incidencia no existe."));
            saveIncidenciaToHistoric(incidencia); // Registrar a l'històric
            redirectAttributes.addFlashAttribute("success", "Estado de la incidencia actualizado correctamente.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/incidencias/list";
    }

    /**
     * Muestra la lista de incidencias.
     *
     * @param model El modelo para pasar la lista de incidencias a la vista.
     * @return La vista con la lista de incidencias.
     */
    @GetMapping("/list")
    public String listIncidencias(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);
        List<Incidencia> incidencias = new ArrayList<>();
        if (authentication != null) {
            model.addAttribute("authorities", authentication.getAuthorities());

            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if (esAdmin) {
                incidencias = incidenciaService.getAllIncidencias();
                model.addAttribute("incidencias", incidencias);
            } else {
                incidencias = incidenciaService.getIncidenciasPropias(authentication);
                model.addAttribute("incidencias", incidencias);
            }

            Map<Long, List<String>> incidenciaImages = new HashMap<>(); // Map para almacenar listas de imágenes
            Map<Long, List<String>> descripcionsImages = new HashMap<>(); // Map para almacenar listas de imágenes

            // Obtener archivos e imágenes para cada incidencia
            for (Incidencia incidencia : incidencias) {
                // Obtener archivos relacionados con la incidencia
                incidencia.setFiles(incidenciaFilesService.getFilesByIncidencia(incidencia.getId()));

                // Obtener imágenes asociadas a la incidencia
                List<byte[]> imageDataList = incidenciaService.getImagesForIncidencia(incidencia.getId());
                List<String> descripcionsList = incidenciaFilesService.getDescripcionsForIncidencia(incidencia.getId());

                List<String> base64Images = new ArrayList<>();

                if (imageDataList != null) {
                    for (byte[] imageData : imageDataList) {
                        if (imageData != null) {
                            String base64Image = Base64.getEncoder().encodeToString(imageData);
                            base64Images.add(base64Image);
                        }
                    }
                }

                System.out.println(incidencia.getDescription());
                // Asociar imágenes y descripciones al ID de la incidencia
                incidenciaImages.put(incidencia.getId(), base64Images);
                descripcionsImages.put(incidencia.getId(), descripcionsList); // Aquí añadimos descripciones
            }

            // Añadir los datos al modelo
            model.addAttribute("incidencias", incidencias);
            model.addAttribute("descripcionsImages", descripcionsImages);
            model.addAttribute("incidenciaImages", incidenciaImages); // Añadir el map con imágenes en Base64
        } else {
            model.addAttribute("incidencias", incidencias);
        }
        return "incidencias-list"; // Retornar la vista
    }

    /**
     * Filtra las incidencias según matrícula y/o fecha.
     *
     * @param matricula La matrícula del vehículo para filtrar las incidencias.
     * @param fecha La fecha para filtrar las incidencias.
     * @param model El modelo para pasar las incidencias filtradas a la vista.
     * @return La vista con la lista de incidencias filtradas.
     */
    @GetMapping("/filter")
    public String filterIncidencias(@RequestParam(required = false) String matricula,
            @RequestParam(required = false) String fecha,
            Model model) {
        List<Incidencia> incidencias;

        if (matricula != null && !matricula.isEmpty() && fecha != null && !fecha.isEmpty()) {
            LocalDate fechaParsed = LocalDate.parse(fecha);
            incidencias = incidenciaService.findByMatriculaAndFecha(matricula, fechaParsed);
        } else if (matricula != null && !matricula.isEmpty()) {
            incidencias = incidenciaService.findByMatricula(matricula);
        } else if (fecha != null && !fecha.isEmpty()) {
            LocalDate fechaParsed = LocalDate.parse(fecha);
            incidencias = incidenciaService.findByFecha(fechaParsed);
        } else {
            incidencias = incidenciaService.getAllIncidencias();
        }

        model.addAttribute("incidencias", incidencias);
        return "incidencias-list";
    }

    @PostMapping("/{id}/upload-file")
    public String uploadFile(@PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            RedirectAttributes redirectAttributes) {
        try {
            // Lógica para procesar el archivo
            incidenciaService.uploadFile(id, file, description);
            redirectAttributes.addFlashAttribute("success", "Archivo subido correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }

        return "redirect:/incidencias/list"; // Redirige a la página de edición de la incidencia
    }

}
