package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Incidencia;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByVehicle_Matricula(String matricula);

    List<Incidencia> findByVehicleMatricula(String matricula);

    List<Incidencia> findByFecha(LocalDate fecha);

    List<Incidencia> findByVehicleMatriculaAndFecha(String matricula, LocalDate fecha);

    List<Incidencia> findByVehicleMatriculaAndOberta(String matricula, boolean oberta);

    boolean existsByVehicle_MatriculaAndOberta(String matricula, boolean oberta);

}
