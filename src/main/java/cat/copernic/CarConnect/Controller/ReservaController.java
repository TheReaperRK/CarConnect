package cat.copernic.CarConnect.Controller;

import cat.copernic.CarConnect.Entity.MongoDB.HistoricReserves;
import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Entity.MySQL.Reserva;
import cat.copernic.CarConnect.Entity.MySQL.Vehicle;
import cat.copernic.CarConnect.Repository.MongoDB.HistoricReservesRepository;
import cat.copernic.CarConnect.Repository.MySQL.ReservaRepository;
import cat.copernic.CarConnect.Service.MongoDB.HistoricReservesService;
import cat.copernic.CarConnect.Service.MySQL.ReservaService;
import cat.copernic.CarConnect.Service.MySQL.ClientService;
import cat.copernic.CarConnect.Service.MySQL.EmailService;
import cat.copernic.CarConnect.Service.MySQL.VehicleService;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador encargado de gestionar las operaciones relacionadas con las reservas de vehículos.
 * Proporciona los endpoints para listar, crear y guardar reservas, así como para manejar
 * la validación y el envío de correos electrónicos de confirmación.
 * 
 * @author Toni
 * @version 1.0
 */

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HistoricReservesRepository historicoReservaRepository;

    @Autowired
    private HistoricReservesService historicReservesService;

    // Mostrar lista de reservas
    @GetMapping("/list")
    public String listReservas(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (authentication != null) {
            model.addAttribute("authorities", authentication.getAuthorities());

            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if (esAdmin) {
                var reservas = reservaService.getAllReservas();
                model.addAttribute("reservas", reservas);
            } else {
                var reservas = reservaService.getReservasPropias(authentication);
                model.addAttribute("reservas", reservas);
            }

        }

        return "reserva-list";
    }

    // Formulario para crear nueva reserva
    @GetMapping("/create")
    public String createReservaForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        // Obtener el cliente logeado usando el servicio de cliente
        String emailLogeado = authentication.getName(); // Asume que el email es el username
        Client clienteLogeado = clientService.getClientByEmail(emailLogeado); // Método que debe existir en tu ClientService

        if (clienteLogeado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }

        // Solo añadir el cliente logeado
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("clients", List.of(clienteLogeado)); // Pasar solo el cliente logeado
        model.addAttribute("vehicles", vehicleService.consultarVehicles());
        return "reserva-form";
    }

    @PostMapping("/create")
    public String saveReserva(@ModelAttribute Reserva reserva, Model model) {
        // Validar que la fecha de devolución no sea anterior a la de recogida
        if (reserva.getDataHoraDevolucio().isBefore(reserva.getDataHoraRecollida())) {
            model.addAttribute("invalidDate", true);
            model.addAttribute("errorMessage", "La fecha de devolución debe ser posterior a la fecha de recogida.");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("vehicles", vehicleService.consultarVehicles());
            return "reserva-form";
        }

        // Validación de solapamiento de reservas usando findByVehicleMatricula
        List<Reserva> reservasExistentes = reservaRepository.findByVehicleMatricula(reserva.getVehicle().getMatricula());
        boolean conflicto = reservasExistentes.stream().anyMatch(r
                -> (reserva.getDataHoraRecollida().isBefore(r.getDataHoraDevolucio())
                && reserva.getDataHoraDevolucio().isAfter(r.getDataHoraRecollida()))
                || (reserva.getDataHoraRecollida().equals(r.getDataHoraRecollida())
                || reserva.getDataHoraDevolucio().equals(r.getDataHoraDevolucio())));

        if (conflicto) {
            model.addAttribute("conflict", true);
            model.addAttribute("errorMessage", "El vehículo ya está reservado en este periodo de tiempo.");
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("vehicles", vehicleService.consultarVehicles());
            return "reserva-form";
        }

        // Validaciones de cliente y vehículo
        if (reserva.getClient() == null || reserva.getClient().getDni() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }
        if (reserva.getVehicle() == null || reserva.getVehicle().getMatricula() == null) {
            throw new RuntimeException("El vehículo es obligatorio");
        }

        // Obtener cliente y vehículo
        Client client = clientService.getClientByDni(reserva.getClient().getDni());
        Vehicle vehicle = vehicleService.getVehicleByMatricula(reserva.getVehicle().getMatricula());
        reserva.setClient(client);
        reserva.setVehicle(vehicle);

        // Calcular precio total
        long dias = ChronoUnit.DAYS.between(reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio());
        double preuBase = dias * vehicle.getPreuPerDia();
        double fianza = vehicle.getFianca();
        double preuTotal = preuBase + fianza;
        reserva.setPreuTotal(preuTotal);

        reservaService.saveReserva(reserva);

        // Crear el contenido del correo electrónico
        String subject = "Confirmación de Reserva";
        Localitzacio localitzacio = vehicle.getLocalitzacio();
        String localitzacioInfo = String.format("%s, %s, %s, %s, %d",
                localitzacio.getCarrer(), localitzacio.getCiutat(),
                localitzacio.getProvincia(), localitzacio.getComunitatAutonoma(),
                localitzacio.getNum());

        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                + "<h2 style='color: #4CAF50;'>¡Reserva Confirmada!</h2>"
                + "<p>Hola <strong>%s</strong>,</p>"
                + "<p>Tu reserva ha sido confirmada. Aquí están los detalles:</p>"
                + "<div style='padding: 15px; border: 1px solid #ddd; background-color: #f9f9f9; border-radius: 5px;'>"
                + "<p><strong>Vehículo:</strong> %s</p>"
                + "<p><strong>Ubicación:</strong> %s</p>"
                + "<p><strong>Fecha de Recogida:</strong> %s</p>"
                + "<p><strong>Fecha de Devolución:</strong> %s</p>"
                + "<p><strong>Precio Total:</strong> %.2f €</p>"
                + "</div>"
                + "<p>Gracias por elegirnos.</p>"
                + "<img src='https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExbnp5dDJleWtwY3k4bmM1YjN1dGl5em0wdnd1eXB1czJqZWk4ejFidiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/OBE6ShkIQQjgQ/giphy.gif' alt='Gracias' style='width: 100%%; max-width: 400px; display: block; margin: 20px auto; border-radius: 5px;'>"
                + "</div>", client.getNombre(), vehicle.getMatricula(), localitzacioInfo,
                reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio(), preuTotal);

        // Enviar correo electrónico
        emailService.sendHtmlEmail(client.getEmail(), subject, htmlContent);

        HistoricReserves historic = new HistoricReserves();
        historic.setReservaId(reserva.getIdReserva());
        historic.setClientDni(reserva.getClient().getDni());
        historic.setVehicleId(reserva.getVehicle().getMatricula());
        historic.setStartDate(reserva.getDataHoraRecollida());
        historic.setEndDate(reserva.getDataHoraDevolucio());
        historic.setTotalPrice(reserva.getPreuTotal());
        historic.setStatus("CREADA");
        historic.setEmail(reserva.getClient().getEmail());
        historic.setNom(reserva.getClient().getNombre());

        historicReservesService.saveHistoricReserve(historic);

        return "redirect:/reservas/list";
    }

    // Formulario para editar reserva existente
    @GetMapping("/edit/{id}")
    public String editReservaForm(@PathVariable Long id, Model model) {
        var reserva = reservaService.getReservaById(id);
        var allVehicles = vehicleService.consultarVehicles();

        // Obtenemos vehículos disponibles, incluyendo el que ya tiene reservado esta reserva
        var reservedVehicles = reservaService.getAllReservas().stream()
                .filter(r -> !r.getIdReserva().equals(id)) // Excluir la reserva actual
                .map(Reserva::getVehicle) // Extrae vehículos reservados de otras reservas
                .collect(Collectors.toList());
        var availableVehicles = allVehicles.stream()
                .filter(vehicle -> !reservedVehicles.contains(vehicle)) // Filtra vehículos disponibles
                .collect(Collectors.toList());

        model.addAttribute("reserva", reserva);
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("vehicles", availableVehicles);
        return "reserva-form";
    }

    /**
     * Marca un vehículo como retornado. Este método marca el vehículo
     * identificado por su matrícula como retornado.
     *
     * @param matricula La matrícula del vehículo a retornar.
     * @return Redirige a la lista de vehículos después de marcarlo como
     * retornado.
     */
    @GetMapping("/return/{id}")
    public String returnVehicle(@PathVariable Long id) {
        reservaService.retornarVehicle(id);
        return "redirect:/reservas/list";
    }

    /**
     * Marca un vehículo como entregado. Este método marca el vehículo
     * identificado por su matrícula como entregado.
     *
     * @param matricula La matrícula del vehículo a entregar.
     * @return Redirige a la lista de vehículos después de marcarlo como
     * entregado.
     */
    @GetMapping("/deliver/{id}")
    public String deliverVehicle(@PathVariable Long id) {
        reservaService.lliurarVehicle(id);
        return "redirect:/reservas/list";
    }

    @PostMapping("/edit/{id}")
    public String updateReserva(@PathVariable Long id, @ModelAttribute Reserva reserva, Model model) {
        try {
            Reserva existingReserva = reservaService.getReservaById(id);

            if (existingReserva != null) {
                // Validar que la fecha de devolución no sea anterior a la fecha de recogida
                if (reserva.getDataHoraDevolucio().isBefore(reserva.getDataHoraRecollida())) {
                    throw new RuntimeException("La fecha de devolución no puede ser anterior a la fecha de recogida");
                }

                // Validar y asignar cliente
                Client client = clientService.getClientByDni(reserva.getClient().getDni());
                if (client == null) {
                    throw new RuntimeException("El cliente especificado no existe.");
                }
                existingReserva.setClient(client);

                // Validar y asignar vehículo
                Vehicle vehicle = vehicleService.getVehicleByMatricula(reserva.getVehicle().getMatricula());
                if (vehicle == null) {
                    throw new RuntimeException("El vehículo especificado no existe.");
                }
                existingReserva.setVehicle(vehicle);

                // Calcular el precio total (Precio base + Fianza)
                long dias = ChronoUnit.DAYS.between(reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio());
                double preuBase = dias * vehicle.getPreuPerDia(); // Precio base por el número de días
                double fianza = vehicle.getFianca(); // Suponiendo que la fianza es un atributo del vehículo
                double preuTotal = preuBase + fianza; // Precio total incluyendo la fianza

                // Establecer el precio total en la reserva
                existingReserva.setPreuTotal(preuTotal);

                // Asignar otros valores de fechas
                existingReserva.setDataHoraRecollida(reserva.getDataHoraRecollida());
                existingReserva.setDataHoraDevolucio(reserva.getDataHoraDevolucio());

                // Guardar la reserva actualizada
                reservaService.saveReserva(existingReserva);
            }

            return "redirect:/reservas/list";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage()); // Mostrar el mensaje de error
            model.addAttribute("reserva", reserva); // Mantener la reserva con los valores introducidos
            model.addAttribute("clients", clientService.getAllClients()); // Lista de clientes
            model.addAttribute("vehicles", vehicleService.consultarVehicles()); // Lista de vehículos
            return "reserva-form"; // Volver a la vista de edición con el error
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.getReservaById(id);

        if (reserva != null) {
            // Crear un histórico antes de eliminar la reserva
            HistoricReserves historic = new HistoricReserves();
            historic.setReservaId(reserva.getIdReserva());
            historic.setClientDni(reserva.getClient().getDni());
            historic.setVehicleId(reserva.getVehicle().getMatricula());
            historic.setStartDate(reserva.getDataHoraRecollida());
            historic.setEndDate(reserva.getDataHoraDevolucio());
            historic.setTotalPrice(reserva.getPreuTotal());
            historic.setStatus("ELIMINADA");
            historic.setEmail(reserva.getClient().getEmail());
            historic.setNom(reserva.getClient().getNombre());

            historicReservesService.saveHistoricReserve(historic);
        }

        reservaService.deleteReserva(id);
        return "redirect:/reservas/list";
    }

@GetMapping("/inici-reserva")
public String iniciReserva(Model model) {
    // Si no existe un atributo "reserva", crea uno nuevo
    if (!model.containsAttribute("reserva")) {
        model.addAttribute("reserva", new Reserva());
    }
    model.addAttribute("fechaHoy", LocalDate.now());
    // Si necesitas datos adicionales, como el vehículo
    if (!model.containsAttribute("vehicle")) {
        model.addAttribute("vehicle", new Vehicle());
    }
    return "inici-reserva";
}


    public List<LocalDate> getFechasReservadasPorVehiculo(String matricula) {
        return reservaRepository.findByVehicleMatricula(matricula)
                .stream()
                .flatMap(reserva -> {
                    LocalDate start = reserva.getDataHoraRecollida();  // Aquí, no es necesario toLocalDate()
                    LocalDate end = reserva.getDataHoraDevolucio();    // Tampoco es necesario toLocalDate()
                    return start.datesUntil(end.plusDays(1)); // Incluye la fecha de devolución
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/selected/{matricula}")
    public String mostrarReservaPorMatricula(@PathVariable String matricula, Model model, Authentication authentication) {
        // Verifica si el usuario está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        // Obtener el cliente logeado usando el servicio de cliente
        String emailLogeado = authentication.getName(); // Asume que el email es el username
        Client clienteLogeado = clientService.getClientByEmail(emailLogeado); // Método que debe existir en tu ClientService

        if (clienteLogeado == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }

        // Obtener el vehículo por matrícula
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
        if (vehicle == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado.");
        }

        // Obtener las reservas existentes para este vehículo
        List<Reserva> reservasExistentes = reservaRepository.findByVehicleMatricula(matricula);

        // Crear una nueva reserva
        Reserva reserva = new Reserva();

        // Validar solapamientos para el periodo actual (opcional si ya tienes fechas preestablecidas)
        boolean conflicto = reservasExistentes.stream().anyMatch(r
                -> (reserva.getDataHoraRecollida() != null && reserva.getDataHoraDevolucio() != null)
                && ((reserva.getDataHoraRecollida().isBefore(r.getDataHoraDevolucio())
                && reserva.getDataHoraDevolucio().isAfter(r.getDataHoraRecollida()))
                || (reserva.getDataHoraRecollida().equals(r.getDataHoraRecollida())
                || reserva.getDataHoraDevolucio().equals(r.getDataHoraDevolucio())))
        );

        if (conflicto) {
            model.addAttribute("conflict", true);
            model.addAttribute("errorMessage", "El vehículo ya está reservado en este periodo de tiempo.");
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("fechaHoy", LocalDate.now());
            model.addAttribute("authorities", authentication.getAuthorities());
            model.addAttribute("clients", clientService.getAllClients());
            model.addAttribute("client", List.of(clienteLogeado).getFirst());
            model.addAttribute("reserva", reserva);
            model.addAttribute("dies", 5); // Ejemplo: Ajustar dinámicamente el número de días
            model.addAttribute("horaris", List.of("09:00", "10:00", "11:00", "12:00", "13:00"));
            return "inici-reserva";
        }

        // Añadir atributos al modelo para una nueva reserva sin conflictos
        List<String> horaris = List.of("09:00", "10:00", "11:00", "12:00", "13:00");
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("vehicle", vehicle);
        
        model.addAttribute("clients", clientService.getAllClients());
        model.addAttribute("client", List.of(clienteLogeado).getFirst());
        model.addAttribute("reserva", reserva);
        model.addAttribute("dies", 5); // Ejemplo: Calcula dinámicamente el número de días
        model.addAttribute("horaris", horaris);

        return "inici-reserva";
    }

    @GetMapping("/search")
    public String buscarReservasPorMatricula(@RequestParam("matricula") String matricula, Model model) {
        // Llamar al repositorio para obtener las reservas con la matrícula proporcionada
        List<Reserva> reservas = reservaRepository.findByVehicleMatricula(matricula);

        // Añadir las reservas al modelo
        model.addAttribute("reservas", reservas);

        // Enviar la matrícula al modelo para mostrarla en el formulario (opcional)
        model.addAttribute("matriculaBuscada", matricula);

        return "reserva-list"; // Asegúrate de que esta es la vista correcta
    }

    @GetMapping("/pagament")
    public String mostrarPagament(@RequestParam String matricula,
            @RequestParam String dniCliente,
            @RequestParam String dataRecollida,
            @RequestParam String dataDevolucio,
            Model model) {

        // Obtener el vehículo
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
        if (vehicle == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado.");
        }

        // Obtener el cliente
        Client client = clientService.getClientByDni(dniCliente);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado.");
        }

        // Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setVehicle(vehicle);
        reserva.setClient(client);
        reserva.setDataHoraRecollida(LocalDate.parse(dataRecollida));
        reserva.setDataHoraDevolucio(LocalDate.parse(dataDevolucio));

        long diasReserva = ChronoUnit.DAYS.between(reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio());
        double precioTotal = diasReserva * vehicle.getPreuPerDia() + vehicle.getFianca();
        reserva.setPreuTotal(precioTotal);

        // Añadir al modelo
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("reserva", reserva);
        model.addAttribute("client", client); // Añadir cliente al modelo

        return "pagament";
    }
    // Método para cancelar la reserva y regresar con los datos introducidos

    @GetMapping("/cancelar")
    public String cancelarReserva(@RequestParam String matricula,
            @RequestParam String dniCliente,
            @RequestParam String dataRecollida,
            @RequestParam String dataDevolucio,
            Model model) {

        // Obtener el vehículo
        Vehicle vehicle = vehicleService.getVehicleByMatricula(matricula);
        if (vehicle == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehículo no encontrado.");
        }

        // Obtener el cliente
        Client client = clientService.getClientByDni(dniCliente);
        if (client == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado.");
        }

        // Crear la reserva (sin guardar) para regresar con los datos previos
        Reserva reserva = new Reserva();
        reserva.setVehicle(vehicle);
        reserva.setClient(client);
        reserva.setDataHoraRecollida(LocalDate.parse(dataRecollida));
        reserva.setDataHoraDevolucio(LocalDate.parse(dataDevolucio));

        // Añadir al modelo
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("reserva", reserva);
        model.addAttribute("client", client); // Añadir cliente al modelo
        model.addAttribute("dies", ChronoUnit.DAYS.between(reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio())); // Calcular días
        model.addAttribute("horaris", List.of("09:00", "10:00", "11:00", "12:00", "13:00")); // Horarios de ejemplo

        // Redirigir a la página de inicio de reserva
        return "redirect:/inici-reserva?matricula=" + matricula + "&dniCliente=" + dniCliente;
    }

@PostMapping("/guardar")
public String guardarReservaUsuario(
        @ModelAttribute Reserva reserva,
        Model model) {

    try {
        if (reserva.getDataHoraRecollida() == null || reserva.getDataHoraDevolucio() == null) {
            throw new RuntimeException("Las fechas de recogida o devolución no pueden ser nulas");
        }

        if (reserva.getDataHoraDevolucio().isBefore(reserva.getDataHoraRecollida())) {
            throw new RuntimeException("La fecha de devolución debe ser posterior a la fecha de recogida.");
        }

        List<Reserva> reservasExistentes = reservaRepository.findByVehicleMatricula(reserva.getVehicle().getMatricula());
        boolean conflicto = reservasExistentes.stream().anyMatch(r
                -> (reserva.getDataHoraRecollida().isBefore(r.getDataHoraDevolucio())
                && reserva.getDataHoraDevolucio().isAfter(r.getDataHoraRecollida()))
                || (reserva.getDataHoraRecollida().equals(r.getDataHoraRecollida())
                || reserva.getDataHoraDevolucio().equals(r.getDataHoraDevolucio())));

        if (conflicto) {
            throw new RuntimeException("El vehículo ya está reservado en este periodo de tiempo.");
        }

        Client client = clientService.getClientByDni(reserva.getClient().getDni());
        if (client == null) {
            throw new RuntimeException("Cliente no encontrado con DNI: " + reserva.getClient().getDni());
        }
        reserva.setClient(client);

        Vehicle vehicle = vehicleService.getVehicleByMatricula(reserva.getVehicle().getMatricula());
        if (vehicle == null) {
            throw new RuntimeException("Vehículo no encontrado con matrícula: " + reserva.getVehicle().getMatricula());
        }
        reserva.setVehicle(vehicle);

        Localitzacio localitzacio = vehicle.getLocalitzacio();
        if (localitzacio == null) {
            throw new RuntimeException("El vehículo no tiene una localización asociada");
        }

        long dias = ChronoUnit.DAYS.between(reserva.getDataHoraRecollida(), reserva.getDataHoraDevolucio());
        double preuBase = dias * vehicle.getPreuPerDia();
        double fianza = vehicle.getFianca();
        double preuTotal = preuBase + fianza;
        reserva.setPreuTotal(preuTotal);

        reservaService.saveReserva(reserva);

        model.addAttribute("reserva", reserva);
        model.addAttribute("clienteNom", reserva.getClient().getNombre());
        model.addAttribute("vehicle", reserva.getVehicle());

        return "confirmacion-reserva";
        //return "confirmacion-reserva";
    } catch (RuntimeException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error-page";
    }
}



/*
    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(Model model) {
        
        // Este método simplemente retorna la vista de confirmación
        return "confirmacion-reserva";
    }
*/
    @GetMapping("/error")
    public String mostrarError(Model model) {
        // Página de error para manejar problemas
        return "error-reserva";
    }
}
