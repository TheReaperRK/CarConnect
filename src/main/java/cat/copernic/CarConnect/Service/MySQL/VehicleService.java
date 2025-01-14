package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Incidencia;
import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Reserva;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Entity.MySQL.VehicleImages;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Repository.MySQL.LocalitzacioRepository;
import cat.copernic.CarConnect.Repository.MySQL.VehicleRepository;
import cat.copernic.CarConnect.Repository.MySQL.IncidenciaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Servicio que gestiona los vehículos en el sistema. Proporciona métodos para
 * crear, modificar, eliminar y consultar vehículos, así como gestionar las
 * imágenes asociadas a ellos.
 */
@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private LocalitzacioRepository localitzacioRepo;
    
    @Autowired
    private AgentRepository agentRepo;
    @Autowired
    private IncidenciaRepository incidenciaRepository;

    /**
     * Obtiene todos los vehículos almacenados en el sistema.
     *
     * @return Una lista de todos los vehículos.
     */
    public List<Vehicle> getAllClients() {
        return vehicleRepository.findAll();
    }

    /**
     * Guarda un nuevo vehículo en el sistema. Verifica que el número de
     * registro no sea nulo antes de guardar.
     *
     * @param vehicle El vehículo a guardar.
     * @throws IllegalArgumentException Si el número de registro es nulo.
     */
    public void saveVehicle(Vehicle vehicle) throws DniDuplicadoException, DniIncorrecteException, IllegalArgumentException {
        if (vehicle.getDescripcio() == null) {
            throw new IllegalArgumentException("El Número de Registro no puede ser nulo");
        }
        validarVehicle(vehicle.getMatricula());
        vehicleRepository.save(vehicle);
    }

    /**
     * Obtiene un vehículo por su matrícula.
     *
     * @param matricula La matrícula del vehículo.
     * @return El vehículo correspondiente.
     * @throws RuntimeException Si no se encuentra el vehículo.
     */
    public Vehicle getVehicleByMatricula(String matricula) {
        return vehicleRepository.findByMatricula(matricula).orElseThrow(() -> new RuntimeException("vehicle no trobat"));
    }

    /**
     * Obtiene la imagen más pequeña asociada a un vehículo por su matrícula.
     *
     * @param matricula La matrícula del vehículo.
     * @return Los datos de la imagen más pequeña, o null si no hay imágenes.
     */
    public byte[] getSmallestImageForVehicle(String matricula) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula).orElse(null);
        if (vehicle != null && vehicle.getImatges() != null && !vehicle.getImatges().isEmpty()) {
            return vehicle.getImatges()
                    .stream()
                    .min(Comparator.comparing(VehicleImages::getId))
                    .map(VehicleImages::getData)
                    .orElse(null); // Retorna la imagen con el ID más bajo
        }
        return null; // Si no hay vehículo o imágenes, retorna null
    }

    public List<byte[]> getRestImagesForVehicle(String matricula) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula).orElse(null);

        if (vehicle != null && vehicle.getImatges() != null && !vehicle.getImatges().isEmpty()) {
            // Encuentra el ID más bajo
            Long smallestId = vehicle.getImatges()
                    .stream()
                    .min(Comparator.comparing(VehicleImages::getId))
                    .map(VehicleImages::getId)
                    .orElse(null);

            // Devuelve todas las imágenes excepto la del ID más bajo
            return vehicle.getImatges()
                    .stream()
                    .filter(image -> !image.getId().equals(smallestId)) // Excluir la de ID más bajo
                    .map(VehicleImages::getData) // Extraer el byte[] de cada imagen
                    .collect(Collectors.toList());
        }

        return Collections.emptyList(); // Retorna lista vacía si no hay vehículo o imágenes
    }

    /**
     * Actualiza un vehículo existente con nuevos datos y una nueva imagen
     * (opcional).
     *
     * @param matricula La matrícula del vehículo a actualizar.
     * @param updatedVehicle Los nuevos datos del vehículo.
     * @param imagen La imagen nueva (opcional).
     * @throws IOException Si ocurre un error al procesar la imagen.
     */
    public void updateVehicle(String matricula, Vehicle updatedVehicle, MultipartFile imagen) throws IOException {
        // Obtener el vehículo que se desea actualizar
        Vehicle vehicle = this.getVehicleByMatricula(matricula);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found for matricula: " + matricula);
        }

        // Actualizar las propiedades con los valores del objeto updatedVehicle
        vehicle.setMarca(updatedVehicle.getMarca());
        vehicle.setModel(updatedVehicle.getModel());
        vehicle.setAny(updatedVehicle.getAny());
        vehicle.setTipusCombustible(updatedVehicle.getTipusCombustible());
        vehicle.setDescripcio(updatedVehicle.getDescripcio());
        vehicle.setPreuPerDia(updatedVehicle.getPreuPerDia());
        vehicle.setFianca(updatedVehicle.getFianca());
        vehicle.setActiu(updatedVehicle.isActiu());
        vehicle.setTipusVehicle(updatedVehicle.getTipusVehicle());
        vehicle.setLocalitzacio(updatedVehicle.getLocalitzacio());

        // Manejo de la imagen (si se proporciona)
        if (imagen != null && !imagen.isEmpty()) {
            VehicleImages vehicleImage = new VehicleImages();
            vehicleImage.setData(imagen.getBytes());
            vehicleImage.setVehicle(vehicle);

            // Inicializar la lista de imágenes si está vacía
            if (vehicle.getImatges() == null) {
                vehicle.setImatges(new ArrayList<>());
            }

            // Añadir la nueva imagen a la lista de imágenes
            vehicle.getImatges().add(vehicleImage);
        }

        // Guardar el vehículo actualizado en la base de datos
        vehicleRepository.save(vehicle);
    }

    /**
     * Elimina un vehículo del sistema.
     *
     * @param dni El número de identificación del vehículo.
     */
    public void deleteClient(String dni) {
        vehicleRepository.deleteById(dni);
    }

    /**
     * Crea un nuevo vehículo y lo guarda en el sistema. Verifica que no exista
     * un vehículo con la misma matrícula.
     *
     * @param vehicle El vehículo a crear.
     * @return El vehículo guardado.
     * @throws IllegalArgumentException Si el vehículo ya existe.
     *//*
    public Vehicle crearVehicle(Vehicle vehicle) {
        // Verificar que no exista un vehículo con la misma matrícula
        if (vehicleRepository.existsByMatricula(vehicle.getMatricula())) {
            throw new IllegalArgumentException("El vehículo con la matrícula especificada ya existe.");
        }
        return vehicleRepository.save(vehicle);
    }*/

    /**
     * Elimina un vehículo del sistema si no tiene reservas activas.
     *
     * @param matricula La matrícula del vehículo.
     * @throws IllegalArgumentException Si el vehículo no existe.
     * @throws IllegalStateException Si el vehículo tiene reservas activas.
     */
    public void esborrarVehicle(String matricula) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));

        // Verificar si el vehículo tiene reservas activas
        if (!vehicle.getReservas().isEmpty() && vehicle.getReservas().stream().anyMatch(reserva -> reserva.isActiu())) {
            throw new IllegalStateException("El vehículo no se puede eliminar porque tiene reservas activas.");
        }

        vehicleRepository.delete(vehicle);
    }

    /**
     * Modifica un vehículo existente.
     *
     * @param matricula La matrícula del vehículo a modificar.
     * @param updatedVehicle Los nuevos datos del vehículo.
     * @return El vehículo actualizado.
     * @throws IllegalArgumentException Si el vehículo no existe.
     */
    public Vehicle modificarVehicle(String matricula, Vehicle updatedVehicle) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));

        vehicle.setMarca(updatedVehicle.getMarca());
        vehicle.setModel(updatedVehicle.getModel());
        vehicle.setDescripcio(updatedVehicle.getDescripcio());
        vehicle.setAny(updatedVehicle.getAny());
        vehicle.setTipusCombustible(updatedVehicle.getTipusCombustible());
        vehicle.setPreuPerDia(updatedVehicle.getPreuPerDia());
        vehicle.setLocalitzacio(updatedVehicle.getLocalitzacio());
        vehicle.setTipusVehicle(updatedVehicle.getTipusVehicle());

        return vehicleRepository.save(vehicle);
    }

    /**
     * Obtiene todos los vehículos del sistema.
     *
     * @return Una lista de todos los vehículos.
     */
    public List<Vehicle> consultarVehicles() {
        return vehicleRepository.findAll();
    }
    
     /**
     * Obtiene los vehículos asociados al agente autenticado en el sistema.
     *
     * @return Una lista de todos los vehículos.
     */
    
     public List<Vehicle> consultarVehiclesPropis(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        List<Agent> agents = agentRepo.findByEmail(username);
        Agent agent = new Agent();
        if(!agents.isEmpty()){
            agent = agents.getFirst();
        }else agent = null;
        
        Localitzacio localitzacio = agent.getLocalitzacio();
        List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio(localitzacio);
       
        
        
        
        return vehicles;


        
    }
    /**
     * Activa un vehículo, cambiando su estado a activo.
     *
     * @param matricula La matrícula del vehículo a activar.
     * @return El vehículo actualizado.
     * @throws IllegalArgumentException Si el vehículo no existe.
     * @throws IllegalStateException Si el vehículo ya está activo.
     */
    public Vehicle activarVehicle(String matricula) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));

        // Verificar si el vehículo ya está activo
        if (vehicle.isActiu()) {
            throw new IllegalStateException("El vehículo ya está activo.");
        }

        vehicle.setActiu(true);
        return vehicleRepository.save(vehicle);
    }
    
    /*
     * Mira si el vehiclulo que estas intentando desactivar tiene alguna reserva activa
     *
     * @param matricula La matrícula del vehículo a mirar.
     * @return Es desactivable o no.
     * @throws IllegalArgumentException Si el vehículo no existe.
     */
    public boolean isDesactivable(String matricula, RedirectAttributes redirectAttributes) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));
        
                boolean desactivable = false;
        
       if ((vehicle.getReservas().isEmpty())){
           desactivable = true;
       } else {
           redirectAttributes.addFlashAttribute("error", "el vehicle amb matricula " + matricula + " Te una o mes reserves obertes");
       }
            
        return desactivable;
    }
    
    /**
     * Mira si el vehiclulo que estas intentando activar tiene alguna incidencia activa
     *
     * @param matricula La matrícula del vehículo a mirar.
     * @return Es activable o no.
     * @throws IllegalArgumentException Si el vehículo no existe.
     */
    public boolean isActivable(String matricula, RedirectAttributes redirectAttributes) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));
            
        List<Incidencia> incidencias = incidenciaRepository.findByVehicleMatricula(matricula);
        
            boolean activable = true;

            if(!(vehicle.getIncidencias().isEmpty())) {
                
                for (Incidencia incidencia: incidencias){
                   if ((incidencia.isOberta()) == true){
                       redirectAttributes.addFlashAttribute("error", "La incidencia amb l'id " + incidencia.getId() + " encara es oberta");
                       activable = false;
                   }
                }
            } 
            
        return activable;
    }

    /**
     * Desactiva un vehículo, cambiando su estado a inactivo.
     *
     * @param matricula La matrícula del vehículo a desactivar.
     * @return El vehículo actualizado.
     * @throws IllegalArgumentException Si el vehículo no existe.
     * @throws IllegalStateException Si el vehículo tiene reservas activas.
     */
    public Vehicle desactivarVehicle(String matricula) {
        Vehicle vehicle = vehicleRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("El vehículo con la matrícula especificada no existe."));

        // Verificar si el vehículo tiene reservas activas
        if (!vehicle.getReservas().isEmpty() && vehicle.getReservas().stream().anyMatch(reserva -> reserva.isActiu())) {
            throw new IllegalStateException("El vehículo no se puede desactivar porque tiene reservas activas.");
        }

        vehicle.setActiu(false);
        return vehicleRepository.save(vehicle);
    }

    

    // Método validar dades d'agent
    public void validarVehicle(String matricula) {

        if (vehicleRepository.existsById(matricula)) { //Busca si existeix l'Agent
            throw new DniDuplicadoException("La matricula del vehicle ya existeix.");

        } else if (!validadorMatricula(matricula)) {
            throw new DniIncorrecteException("La matricula es incorrecte.");
        }

    }

    public boolean validadorMatricula(String matricula) {
        Pattern patro = Pattern.compile("^\\d{4}[A-Z]{3}$");
        Matcher matcher = patro.matcher(matricula);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
