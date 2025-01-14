package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricReserves;
import cat.copernic.CarConnect.Service.MongoDB.HistoricReservesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Objects;

/**
 * Controlador para gestionar las reservas históricas.
 * Proporciona endpoints para listar todas las reservas y generar reportes detallados de reservas.
 * @author Carlos
 */
@Controller
@RequestMapping("/historic")
public class HistoricReservesController {

    @Autowired
    private HistoricReservesService historicReservesService;

    /**
     * Muestra una lista de todas las reservas históricas.
     *
     * @param model el objeto modelo que se usa para pasar datos a la vista
     * @return el nombre de la vista que muestra la lista de reservas históricas
     */
    @GetMapping
    public String listHistoricReservas(Model model) {
        List<HistoricReserves> historicReserves = historicReservesService.findAll();
        model.addAttribute("historicReserves", historicReserves);
        return "historic-list"; // Nombre de la vista para listar reservas históricas
    }

    /**
     * Genera un informe detallado de reservas, con la opción de filtrar por el DNI del cliente.
     * 
     * Si se especifica un DNI, el informe solo incluye las reservas asociadas con ese cliente.
     * Los resultados se ordenan primero por el DNI del cliente y luego por la fecha de inicio (más reciente primero).
     * También se calculan el número total de reservas y el precio total.
     *
     * @param clientDni el DNI del cliente para filtrar las reservas (opcional)
     * @param model     el objeto modelo que se usa para pasar datos a la vista
     * @return el nombre de la vista que muestra el informe de reservas
     */
    @GetMapping("/informe-reservas")
    public String informeReserves(@RequestParam(required = false) String clientDni, Model model) {
        // Obtener todas las reservas
        List<HistoricReserves> historicReserves = historicReservesService.findAll();

        // Filtrar por DNI del cliente si se proporciona
        if (clientDni != null && !clientDni.isEmpty()) {
            historicReserves = historicReserves.stream()
                    .filter(reserva -> Objects.equals(reserva.getClientDni(), clientDni))
                    .collect(Collectors.toList());
        }

        // Ordenar por DNI del cliente y luego por fecha de inicio (más reciente primero)
        historicReserves = historicReserves.stream()
                .sorted(Comparator.comparing(HistoricReserves::getClientDni, Comparator.nullsLast(String::compareTo))
                        .thenComparing(HistoricReserves::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // Calcular totales
        long totalReserves = historicReserves.size();
        double totalPrice = historicReserves.stream().mapToDouble(HistoricReserves::getTotalPrice).sum();

        // Pasar datos al modelo
        model.addAttribute("historicReserves", historicReserves);
        model.addAttribute("totalReserves", totalReserves);
        model.addAttribute("totalPrice", totalPrice);

        return "historic-informe"; // Nombre de la vista para el informe de reservas
    }
}
