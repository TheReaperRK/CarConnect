package cat.copernic.CarConnect.Repository.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricIncidencies;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricIncidenciesRepository extends MongoRepository<HistoricIncidencies, String> {
    List<HistoricIncidencies> findByVehicleId(String vehicleId);
}
