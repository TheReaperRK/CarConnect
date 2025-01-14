package cat.copernic.CarConnect.Entity.MongoDB;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;



/**
 * Clase que representa las incidencias históricas asociadas a un vehículo.
 * Se utiliza para almacenar información sobre incidencias, incluyendo descripción, 
 * costo, identificación del vehículo, estado de apertura y fecha.
 * 
 * Este modelo está diseñado para persistirse en una colección de MongoDB.
 * 
 * @author Toni
 */

@Document(collection = "historic_incidencies")
public class HistoricIncidencies {

    @Id
    private String id;
    private String description;
    private double cost;
    private String vehicleId;  // Usamos matrícula como vehicleId
    private boolean aberta;  // Aquí es 'aberta', que es el campo que se corresponde con 'oberta' en MySQL
    private LocalDate fecha;

    // Campo transitorio
    private transient String fechaFormateada;

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isAberta() {
        return aberta;
    }

    public void setAberta(boolean aberta) {
        this.aberta = aberta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getFechaFormateada() {
        return fechaFormateada;
    }

    public void setFechaFormateada(String fechaFormateada) {
        this.fechaFormateada = fechaFormateada;
    }
}

