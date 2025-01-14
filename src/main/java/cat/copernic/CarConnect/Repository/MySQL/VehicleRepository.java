package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    // Buscar vehículo por matrícula
    Optional<Vehicle> findByMatricula(String matricula);

// Buscar todos los vehículos activos
    List<Vehicle> findByActiuTrue();
    List<Vehicle> findByLocalitzacio(Localitzacio localitzacio);
    // Verificar si un vehículo existe por matrícula
    boolean existsByMatricula(String matricula);

    // Buscar vehículos con reservas activas
    List<Vehicle> findByReservas_ActiuTrue();  // Asumiendo que tienes una relación de Reservas en la entidad Vehicle

    // Otros métodos si fueran necesarios
}
