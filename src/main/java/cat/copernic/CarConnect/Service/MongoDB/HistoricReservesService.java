package cat.copernic.CarConnect.Service.MongoDB;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricReserves;
import cat.copernic.CarConnect.Repository.MongoDB.HistoricReservesRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona las operaciones relacionadas con el historial de
 * reservas. Permite guardar o actualizar registros del historial y consultar la
 * información de reservas a partir del ID de reserva o el DNI del cliente.
 */
@Service
public class HistoricReservesService {

    @Autowired
    private HistoricReservesRepository historicReservesRepository;

    public void saveHistoricReserve(HistoricReserves historicReserves) {
        historicReservesRepository.save(historicReserves);
    }

    public List<HistoricReserves> findAll() {
        return historicReservesRepository.findAll();
    }
}
