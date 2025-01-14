package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Reserva;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.query.Param;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Método para buscar reservas por la matrícula del vehículo
    List<Reserva> findByVehicleMatricula(String matricula);
    List<Reserva> findByClientDni(String dni);
    // Consulta personalizada para verificar si el vehículo está reservado en un rango de fechas
    @Query("SELECT COUNT(r) FROM Reserva r "
            + "WHERE r.vehicle.matricula = :matricula "
            + "AND r.dataHoraRecollida <= :fin "
            + "AND r.dataHoraDevolucio >= :inicio")
    int countReservasSolapadas(@Param("matricula") String matricula,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);


}
