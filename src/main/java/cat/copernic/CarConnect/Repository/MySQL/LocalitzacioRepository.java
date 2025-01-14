package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocalitzacioRepository extends JpaRepository<Localitzacio, String>, JpaSpecificationExecutor<Localitzacio> {

    @Query("SELECT l FROM Localitzacio l WHERE l.codiPostal = :codiPostal AND l.comunitatAutonoma = :comunitatAutonoma AND l.provincia = :provincia AND l.ciutat = :ciutat")
    List<Localitzacio> findByFilters(
            @Param("codiPostal") String codiPostal,
            @Param("comunitatAutonoma") String comunitatAutonoma,
            @Param("provincia") String provincia,
            @Param("ciutat") String ciutat
    );
}
