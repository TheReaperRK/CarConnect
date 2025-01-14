package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Reserva;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Repository.MySQL.ClientRepository;
import cat.copernic.CarConnect.Repository.MySQL.ReservaRepository;
import cat.copernic.CarConnect.Repository.MySQL.VehicleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Servicio que gestiona las reservas de vehículos en el sistema. Proporciona
 * métodos para crear, modificar, eliminar y consultar reservas.
 */
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private AgentRepository agentRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     * Obtiene todas las reservas almacenadas en el sistema. Forza la
     * inicialización de las relaciones Lazy con Client y Vehicle.
     *
     * @return Una lista de todas las reservas.
     */
    public List<Reserva> getAllReservas() {
        List<Reserva> reservas = reservaRepository.findAll();

        // Forzar la inicialización de relaciones Lazy
        reservas.forEach(reserva -> {
            reserva.getClient().getNombre(); // Inicializa la relación con Client
            reserva.getVehicle().getMarca(); // Inicializa la relación con Vehicle
        });

        return reservas;
    }
    /**
     * Obtiene  las reservas de los vehiculos del usuario autenticado almacenadas en el sistema. Forza la
     * inicialización de las relaciones Lazy con Client y Vehicle.
     *
     * @return Una lista de todas las reservas.
     */
    public List<Reserva> getReservasPropias(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        List<Client> clients = clientRepo.findByEmail(username);
        List<Agent> agents = agentRepo.findByEmail(username);
        List<Reserva> reservas = new ArrayList<>();
        
        Agent agent = new Agent();
         
        if(!agents.isEmpty()){
            
            
            agent = agents.getFirst();
            Localitzacio localitzacio = agent.getLocalitzacio();
            List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio(localitzacio);
            if(!vehicles.isEmpty()){
                for(Vehicle vehicle : vehicles){
                   List<Reserva> reserv = reservaRepository.findByVehicleMatricula(vehicle.getMatricula());
                   if(!reserv.isEmpty()){
                       reservas.add(reserv.getFirst());
                   }
                    
                }
               
            }
            
        }else if(!clients.isEmpty()){
                Client client = new Client();
                client = clients.getFirst();
                reservas = reservaRepository.findByClientDni(client.getDni());
                
        }else{
            agent = null;
        }
        
        
        // Forzar la inicialización de relaciones Lazy
        reservas.forEach(reserva -> {
            reserva.getClient().getNombre(); // Inicializa la relación con Client
            reserva.getVehicle().getMarca(); // Inicializa la relación con Vehicle
        });

        return reservas;
    }

    /**
     * Obtiene una reserva por su ID.
     *
     * @param id El ID de la reserva.
     * @return La reserva encontrada.
     * @throws RuntimeException Si no se encuentra la reserva.
     */
    public Reserva getReservaById(Long id) {
        return reservaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no trobada"));
    }

    /**
     * Marca un vehículo como retornado, desactivando su reserva activa.
     *
     * @param matricula La matrícula del vehículo a retornar.
     * @throws IllegalArgumentException Si el vehículo no existe.
     * @throws IllegalStateException Si no hay reservas activas para el
     * vehículo.
     */
    public void retornarVehicle(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("la reserva especificada no existe."));

        reserva.setRetornat(true);
        reservaRepository.save(reserva);  // Guardar cambios en el vehículo
    }

    /**
     * Marca un vehículo como entregado, finalizando su reserva activa.
     *
     * @param matricula La matrícula del vehículo a entregar.
     * @throws IllegalArgumentException Si el vehículo no existe.
     * @throws IllegalStateException Si no hay reservas activas para el
     * vehículo.
     */
    public void lliurarVehicle(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("la reserva especificada no existe."));

        reserva.setLliurat(true);
        reservaRepository.save(reserva);  // Guardar cambios en el vehículo
    }
    
    /**
     * Guarda una nueva reserva en el sistema.
     *
     * @param reserva La reserva a guardar.
     */
    public void saveReserva(Reserva reserva) {
        reservaRepository.save(reserva);
    }

    /**
     * Actualiza los datos de una reserva existente. Solo se actualizan los
     * campos que han cambiado respecto a la reserva original.
     *
     * @param id El ID de la reserva a actualizar.
     * @param updatedReserva Los nuevos datos de la reserva.
     */
    public void updateReserva(Long id, Reserva updatedReserva) {
        Reserva reserva = getReservaById(id);

        // Solo actualizamos si hay cambios
        if (!reserva.getClient().equals(updatedReserva.getClient())) {
            reserva.setClient(updatedReserva.getClient());
        }
        if (!reserva.getVehicle().equals(updatedReserva.getVehicle())) {
            reserva.setVehicle(updatedReserva.getVehicle());
        }
        reserva.setDataHoraRecollida(updatedReserva.getDataHoraRecollida());
        reserva.setDataHoraDevolucio(updatedReserva.getDataHoraDevolucio());
        reserva.setDataCancelacio(updatedReserva.getDataCancelacio());
        reserva.setPreuTotal(updatedReserva.getPreuTotal());
        reserva.setFianca(updatedReserva.getFianca());
        reserva.setActiu(updatedReserva.isActiu());
        reserva.setAsseguranca(updatedReserva.getAsseguranca());

        reservaRepository.save(reserva);  // Guardamos la reserva actualizada
    }

    /**
     * Elimina una reserva del sistema.
     *
     * @param id El ID de la reserva a eliminar.
     */
    public void deleteReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    /**
     * Obtiene todas las reservas asociadas a un vehículo por su matrícula.
     *
     * @param matricula La matrícula del vehículo.
     * @return Una lista de reservas asociadas al vehículo.
     */
    public List<Reserva> getReservasByVehicle(String matricula) {
        return reservaRepository.findByVehicleMatricula(matricula);
    }


    /**
     * Obtiene las fechas reservadas para un vehículo dado su matrícula. Este
     * método devuelve todas las fechas en las que el vehículo está reservado,
     * desde la fecha de recogida hasta la fecha de devolución (inclusive).
     *
     * @param matricula La matrícula del vehículo.
     * @return Una lista de fechas reservadas para el vehículo.
     */

    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    public List<LocalDate> getFechasReservadasPorVehiculo(String matricula) {
        return reservaRepository.findByVehicleMatricula(matricula)
                .stream()
                .flatMap(reserva -> {
                    LocalDate start = reserva.getDataHoraRecollida();  // Aquí, no es necesario toLocalDate()
                    LocalDate end = reserva.getDataHoraDevolucio();    // Tampoco es necesario toLocalDate()
                    return start.datesUntil(end.plusDays(1)); // Incluye la fecha de devolución
                })
                .collect(Collectors.toList());
    }


    public List<Reserva> getReservasByVehicleAndDates(String matricula, LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findAll().stream()
                .filter(reserva -> reserva.getVehicle().getMatricula().equals(matricula)
                && (fechaInicio.isBefore(reserva.getDataHoraDevolucio())
                && fechaFin.isAfter(reserva.getDataHoraRecollida())))
                .collect(Collectors.toList());
    }

    public int contarReservasSolapadas(String matricula, LocalDate inicio, LocalDate fin) {
        return reservaRepository.countReservasSolapadas(matricula, inicio, fin);
    }

}
