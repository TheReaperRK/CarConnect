package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Incidencia;
import cat.copernic.CarConnect.Entity.MySQL.IncidenciaFiles;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Reserva;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Repository.MySQL.IncidenciaFilesRepository;
import cat.copernic.CarConnect.Repository.MySQL.IncidenciaRepository;
import cat.copernic.CarConnect.Repository.MySQL.VehicleRepository;
import jakarta.persistence.criteria.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio que gestiona las incidencias asociadas a los vehículos. Proporciona
 * métodos para crear, actualizar, eliminar y consultar incidencias.
 */
@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;
    
    @Autowired
    private IncidenciaFilesRepository incidenciaFilesRepository;


    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private AgentRepository agentRepo;
    /**
     * Obtiene todas las incidencias registradas en el sistema.
     *
     * @return Una lista con todas las incidencias.
     */
    public List<Incidencia> getAllIncidencias() {
        return incidenciaRepository.findAll();
    }
    
    public List<Incidencia> getIncidenciasPropias(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        List<Agent> agents = agentRepo.findByEmail(username);
        Agent agent = new Agent();
         List<Incidencia> incidencias = new ArrayList<>();
        if(!agents.isEmpty()){
            agent = agents.getFirst();
            Localitzacio localitzacio = agent.getLocalitzacio();
            List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio(localitzacio);
            if(!vehicles.isEmpty()){
                for(Vehicle vehicle : vehicles){
                   List<Incidencia> incidenc = incidenciaRepository.findByVehicleMatricula(vehicle.getMatricula());
                   if(!incidenc.isEmpty()){
                       incidencias.add(incidenc.getFirst());
                   }
                    
                }
               
            }
            
        }else agent = null;
        
            return incidencias;
        }
    /**
     * Obtiene una incidencia por su ID.
     *
     * @param id El ID de la incidencia a obtener.
     * @return Un Optional con la incidencia si existe, o un Optional vacío si
     * no.
     */
    public Optional<Incidencia> getIncidenciaById(Long id) {
        return incidenciaRepository.findById(id);
    }

    /**
     * Guarda una nueva incidencia en el sistema. Se verifica que el vehículo
     * esté asociado correctamente y que no haya incidencias abiertas para el
     * mismo vehículo.
     *
     * @param incidencia La incidencia a guardar.
     * @throws IllegalArgumentException Si no se asocia un vehículo válido.
     * @throws IllegalStateException Si ya existe una incidencia abierta para el
     * vehículo.
     */
    public void saveIncidencia(Incidencia incidencia) {
        if (incidencia.getVehicle() == null || incidencia.getVehicle().getMatricula() == null) {
            throw new IllegalArgumentException("Debe asociar una incidencia a un vehículo válido.");
        }

        Vehicle vehicle = vehicleService.getVehicleByMatricula(incidencia.getVehicle().getMatricula());

        // Verificar si ya existe una incidencia abierta para el vehículo
        List<Incidencia> incidenciasAbiertas = incidenciaRepository.findByVehicleMatriculaAndOberta(vehicle.getMatricula(), true);
        if (!incidenciasAbiertas.isEmpty()) {
            throw new IllegalStateException("El vehículo ya tiene una incidencia abierta.");
        }

        incidencia.setVehicle(vehicle);
        incidencia.setOberta(true); // Marcar como abierta si es nueva
        incidenciaRepository.save(incidencia);
    }

    /**
     * Actualiza los detalles de una incidencia existente. Si el vehículo
     * asociado cambia, se actualiza con el nuevo vehículo.
     *
     * @param incidencia La incidencia con los datos actualizados.
     */
    public void updateIncidencia(Incidencia incidencia) {
        if (incidencia.getId() != null) {
            Optional<Incidencia> existingIncidencia = incidenciaRepository.findById(incidencia.getId());
            if (existingIncidencia.isPresent()) {
                Incidencia updatedIncidencia = existingIncidencia.get();
                updatedIncidencia.setDescription(incidencia.getDescription());
                updatedIncidencia.setCost(incidencia.getCost());
                updatedIncidencia.setOberta(incidencia.isOberta()); // Actualizar estado

                if (incidencia.getVehicle() != null && incidencia.getVehicle().getMatricula() != null) {
                    Vehicle vehicle = vehicleService.getVehicleByMatricula(incidencia.getVehicle().getMatricula());
                    updatedIncidencia.setVehicle(vehicle);
                }

                incidenciaRepository.save(updatedIncidencia);
            }
        }
    }

    /**
     * Desactiva una incidencia, cambiando su estado a cerrado (Oberta a false).
     *
     * @param id El ID de la incidencia a desactivar.
     */
    public void deactivateIncidencia(Long id) {
        Optional<Incidencia> incidenciaOptional = incidenciaRepository.findById(id);

        if (incidenciaOptional.isPresent()) {
            Incidencia incidencia = incidenciaOptional.get();
            incidencia.setOberta(false); // Desactivar la incidencia
            incidenciaRepository.save(incidencia);
        }
    }

    /**
     * Obtiene todas las incidencias asociadas a un vehículo específico mediante
     * su matrícula.
     *
     * @param matricula La matrícula del vehículo.
     * @return Una lista con las incidencias asociadas al vehículo.
     */
    public List<Incidencia> findByMatricula(String matricula) {
        return incidenciaRepository.findByVehicleMatricula(matricula);
    }

    /**
     * Obtiene todas las incidencias que ocurrieron en una fecha específica.
     *
     * @param fecha La fecha en la que ocurrieron las incidencias.
     * @return Una lista con las incidencias ocurridas en la fecha
     * proporcionada.
     */
    public List<Incidencia> findByFecha(LocalDate fecha) {
        return incidenciaRepository.findByFecha(fecha);
    }

    /**
     * Obtiene incidencias asociadas a un vehículo y que ocurrieron en una fecha
     * específica.
     *
     * @param matricula La matrícula del vehículo.
     * @param fecha La fecha en la que ocurrieron las incidencias.
     * @return Una lista con las incidencias asociadas al vehículo en la fecha
     * proporcionada.
     */
    public List<Incidencia> findByMatriculaAndFecha(String matricula, LocalDate fecha) {
        return incidenciaRepository.findByVehicleMatriculaAndFecha(matricula, fecha);
    }

    /**
     * Verifica si un vehículo tiene alguna incidencia abierta.
     *
     * @param vehicleId El ID del vehículo.
     * @return true si el vehículo tiene alguna incidencia abierta, false si no.
     */
    public boolean vehicleHasOpenIncidencia(String vehicleId) {
        return incidenciaRepository.existsByVehicle_MatriculaAndOberta(vehicleId, true);
    }

    /**
     * Cambia el estado de una incidencia entre abierta y cerrada. Si se intenta
     * abrir una incidencia, se verifica que no haya otras incidencias abiertas
     * para el mismo vehículo.
     *
     * @param id El ID de la incidencia a alternar.
     * @throws IllegalArgumentException Si la incidencia no existe.
     * @throws IllegalStateException Si ya existe una incidencia abierta para el
     * vehículo.
     */
    public void toggleIncidenciaState(Long id) {
        Optional<Incidencia> incidenciaOptional = incidenciaRepository.findById(id);

        if (incidenciaOptional.isPresent()) {
            Incidencia incidencia = incidenciaOptional.get();

            // Si se intenta activar, verificar que no haya otras incidencias abiertas
            if (!incidencia.isOberta()) {
                List<Incidencia> incidenciasAbiertas = incidenciaRepository.findByVehicleMatriculaAndOberta(
                        incidencia.getVehicle().getMatricula(), true
                );
                if (!incidenciasAbiertas.isEmpty()) {
                    throw new IllegalStateException("El vehículo ya tiene una incidencia abierta.");
                }
            }

            // Alternar estado
            incidencia.setOberta(!incidencia.isOberta());
            incidenciaRepository.save(incidencia);
        } else {
            throw new IllegalArgumentException("La incidencia no existe.");
        }
    }
    
    public void uploadFile(Long incidenciaId, MultipartFile file, String description) throws IOException {
    // Verifica que la incidencia exista
    Optional<Incidencia> optionalIncidencia = getIncidenciaById(incidenciaId);
    if (optionalIncidencia.isEmpty()) {
        throw new IllegalArgumentException("La incidencia con ID " + incidenciaId + " no existe.");
    }

    Incidencia incidencia = optionalIncidencia.get();

    // Convierte el archivo a un array de bytes
    byte[] fileBytes = file.getBytes();
    
    // Crea el objeto Archivo y lo guarda en la base de datos
    IncidenciaFiles archivo = new IncidenciaFiles();
    archivo.setFileData(fileBytes);
    archivo.setDescription(description);
    archivo.setIncidencia(incidencia); // Establece la relación con la incidencia

    // Guarda el archivo en la base de datos
    incidenciaFilesRepository.save(archivo);  // Suponiendo que tienes un repositorio de archivos
}

    public List<byte[]> getImagesForIncidencia(Long id) {
            Incidencia incidencia = incidenciaRepository.findById(id).orElse(null);

            if (incidencia != null && !incidencia.getFiles().isEmpty()) {
               
                // Devuelve todas las imágenes excepto la del ID más bajo
                return incidencia.getFiles()
                              .stream()
                              .map(IncidenciaFiles::getFileData) // Extraer el byte[] de cada imagen
                              .collect(Collectors.toList());
            }

            return Collections.emptyList(); // Retorna lista vacía si no hay vehículo o imágenes
        }
    

}
