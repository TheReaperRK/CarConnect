package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricIncidencies;
import cat.copernic.CarConnect.Service.MongoDB.HistoricIncidenciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador para gestionar las incidencias históricas. Este controlador
 * maneja las peticiones HTTP para listar y filtrar las incidencias históricas
 * asociadas a vehículos.
 *
 * <p>
 * El controlador interactúa con el servicio {@link HistoricIncidenciesService}
 * para obtener y filtrar los historiales de incidencias.</p>
 *
 * @author David
 */
@Controller
@RequestMapping("/historic")
public class HistoricIncidenciesController {

    @Autowired
    private HistoricIncidenciesService historicService;  // Servicio para gestionar las incidencias históricas

    /**
     * Maneja la ruta "/historic-incidencies" para listar las incidencias
     * históricas.
     */
    @GetMapping("/historic-incidencies")
    public String getHistoricIncidencies(Model model) {
        List<HistoricIncidencies> historics = historicService.getAllHistorics();

        // Formatear fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (HistoricIncidencies historic : historics) {
            if (historic.getFecha() != null) {
                historic.setFechaFormateada(historic.getFecha().format(formatter));
            }
        }

        model.addAttribute("historics", historics);
        return "historic-list-incidencies"; // Nombre del template a renderizar
    }

    /**
     * Maneja la ruta "/historic-incidencia" para redirigir o manejar el caso
     * específico.
     */
    @GetMapping("/historic-incidencia")
    public String handleHistoricIncidencia(Model model) {
        // En este ejemplo, simplemente redirige a "historic-incidencies".
        return "redirect:/historic/historic-incidencies";
    }

    @GetMapping("/informe")
    public String getVehicleReport(String vehicleId, Model model) {
        // Obtener incidencias filtradas por vehicleId o todas
        List<HistoricIncidencies> historics = (vehicleId == null || vehicleId.isEmpty())
                ? historicService.getAllHistorics()
                : historicService.getHistoricsByVehicle(vehicleId);

        // Ordenar: primero las fechas válidas en orden descendente, luego las nulas
        historics.sort((a, b) -> {
            if (a.getFecha() == null && b.getFecha() == null) {
                return 0;
            }
            if (a.getFecha() == null) {
                return 1; // Fecha nula va al final
            }
            if (b.getFecha() == null) {
                return -1;
            }
            return b.getFecha().compareTo(a.getFecha());
        });

        // Calcular totales
        int totalIncidencies = historics.size();
        double totalCost = historics.stream()
                .filter(h -> h.getCost() != 0) // Validar costo no nulo
                .mapToDouble(HistoricIncidencies::getCost)
                .sum();

        // Añadir datos al modelo
        model.addAttribute("historics", historics);
        model.addAttribute("totalIncidencies", totalIncidencies);
        model.addAttribute("totalCost", totalCost);

        return "informe-historic"; // Nombre del template del informe
    }

}
