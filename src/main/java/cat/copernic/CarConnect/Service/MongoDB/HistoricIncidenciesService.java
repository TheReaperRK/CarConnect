package cat.copernic.CarConnect.Service.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricIncidencies;
import cat.copernic.CarConnect.Repository.MongoDB.HistoricIncidenciesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HistoricIncidenciesService {

    @Autowired
    private HistoricIncidenciesRepository historicIncidenciesRepository;

    // Método para obtener todas las incidencias históricas
    public List<HistoricIncidencies> getAllHistorics() {
        // Utiliza el repositorio para obtener todos los registros
        return historicIncidenciesRepository.findAll();
    }

    public void saveToHistoric(HistoricIncidencies historicIncidencies) {
        historicIncidenciesRepository.save(historicIncidencies);
    }
    
    public List<HistoricIncidencies> getHistoricsByVehicle(String vehicleId) {
    return historicIncidenciesRepository.findByVehicleId(vehicleId);
}
}
