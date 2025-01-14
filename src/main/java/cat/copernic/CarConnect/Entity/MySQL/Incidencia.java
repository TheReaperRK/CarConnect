package cat.copernic.CarConnect.Entity.MySQL;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate; // Para usar LocalDate
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;


/**
 * Representa una incidencia registrada en el sistema CarConnect.
 * <p>
 * Cada incidencia está asociada a un vehículo y contiene información como una
 * descripción, el coste, la fecha, y si está activa o no.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "incidencias")
public class Incidencia {

    /**
     * Identificador único de la incidencia. Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Descripción de la incidencia.
     */
    private String description;

    /**
     * Coste asociado a la incidencia. Debe ser un valor positivo y no puede ser
     * nulo.
     */
    @NotNull(message = "El coste no puede ser nulo.")
    @Min(value = 0, message = "El coste debe ser un valor positivo.")
    private double cost;

    /**
     * Vehículo asociado a la incidencia. Referencia a la matrícula del
     * vehículo.
     */
    @ManyToOne
    @JoinColumn(name = "matricula", nullable = true)
    private Vehicle vehicle;

    /**
     * Indica si la incidencia está abierta (activa) o no. Valor por defecto:
     * {@code false}.
     */
    private boolean oberta = false;

    /**
     * Fecha en la que ocurrió o se registró la incidencia.
     */
    private LocalDate fecha;

    /**
     * Propiedad transitoria utilizada únicamente en la vista para determinar si
     * se muestran los detalles de la incidencia. No se persiste en la base de
     * datos.
     */
    @Transient

    private boolean showDetails = false; // Esta propiedad se usará solo en la vista
    
    @OneToMany(mappedBy = "incidencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidenciaFiles> files = new ArrayList<>();

    /**
     * Obtiene el valor de la propiedad {@code showDetails}.
     *
     * @return {@code true} si se muestran los detalles de la incidencia,
     * {@code false} en caso contrario.
     */
    public boolean isShowDetails() {
        return showDetails;
    }

    /**
     * Establece el valor de la propiedad {@code showDetails}.
     *
     * @param showDetails {@code true} para mostrar los detalles de la
     * incidencia, {@code false} en caso contrario.
     */
    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }

    /**
     * Obtiene el identificador único de la incidencia.
     *
     * @return Identificador único de la incidencia.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único de la incidencia.
     *
     * @param id Nuevo identificador único de la incidencia.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene la descripción de la incidencia.
     *
     * @return Descripción de la incidencia.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción de la incidencia.
     *
     * @param description Nueva descripción de la incidencia.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene el coste asociado a la incidencia.
     *
     * @return Coste de la incidencia.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Establece el coste asociado a la incidencia. Las validaciones para el
     * coste se aplican a través de las anotaciones {@link Min} y
     * {@link NotNull}.
     *
     * @param cost Nuevo coste de la incidencia.
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Obtiene el vehículo asociado a la incidencia.
     *
     * @return Vehículo asociado a la incidencia.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Establece el vehículo asociado a la incidencia.
     *
     * @param vehicle Nuevo vehículo asociado a la incidencia.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Verifica si la incidencia está abierta (activa).
     *
     * @return {@code true} si la incidencia está abierta, {@code false} en caso
     * contrario.
     */
    public boolean isOberta() {
        return oberta;
    }

    /**
     * Establece si la incidencia está abierta (activa).
     *
     * @param oberta {@code true} para marcar la incidencia como abierta,
     * {@code false} para marcarla como cerrada.
     */
    public void setOberta(boolean oberta) {
        this.oberta = oberta;
    }

    /**
     * Obtiene la fecha de la incidencia.
     *
     * @return Fecha de la incidencia.
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de la incidencia.
     *
     * @param fecha Nueva fecha de la incidencia.
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    // Métodos para agregar/eliminar archivos
    public void addFile(IncidenciaFiles file) {
        files.add(file);
        file.setIncidencia(this);
    }

    public void removeFile(IncidenciaFiles file) {
        files.remove(file);
        file.setIncidencia(null);
    }


    public void setFiles(List<IncidenciaFiles> files) {
    this.files = files; // Asignar la lista de archivos
}

    public List<IncidenciaFiles> getFiles() {
        return files;
    }
}
