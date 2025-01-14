package cat.copernic.CarConnect.Repository.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricReserves;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricReservesRepository extends MongoRepository<HistoricReserves, String> {

    Long countAllById(String id);

    @Query(value = "{id:  ?0}", count = true)
    Long countAllByIdQuery(String id);

    // Buscar historial de reservas por ID de reserva
    HistoricReserves findByReservaId(Long reservaId);

    // Buscar historial de reservas por DNI de cliente
    HistoricReserves findByClientDni(String clientDni);
}
