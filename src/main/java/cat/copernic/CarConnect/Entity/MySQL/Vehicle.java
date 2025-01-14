package cat.copernic.CarConnect.Entity.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusVehicle;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Representa un vehículo dentro del sistema CarConnect.
 * <p>
 * Esta clase gestiona la información relacionada con los vehículos disponibles
 * para la reserva, incluyendo datos de identificación, tipo, precio, y
 * relaciones con otras entidades como ubicaciones, imágenes, reservas e
 * incidencias.
 * </p>
 *
 * @author Carlos
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Vehicle {

    /**
     * Matrícula única que identifica al vehículo.
     */
    @Id
    @Column(unique = true, nullable = false)
    private String matricula;

    /**
     * Marca del vehículo (e.g., Toyota, Ford).
     */
    @Column(nullable = false)
    private String marca;

    /**
     * Modelo del vehículo (e.g., Corolla, Mustang).
     */
    @Column(nullable = false)
    private String model;

    /**
     * Número de registro adicional del vehículo (si aplica).
     */
    @Column(nullable = false)
    private String descripcio;

    /**
     * Año de fabricación del vehículo.
     */
    private int any;

    /**
     * Tipo de combustible que utiliza el vehículo (e.g., gasolina, diésel,
     * eléctrico).
     */
    private String tipusCombustible;

    /**
     * Precio por día para alquilar el vehículo.
     */
    @Column(nullable = false)
    private double preuPerDia;

    /**
     * Indica si el vehículo está activo y disponible para reservas.
     */
    @Column(nullable = false)
    private boolean actiu;

    /**
     * Fianza requerida para alquilar el vehículo.
     */
    private double fianca;

    /**
     * Lista de imágenes asociadas al vehículo.
     */
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleImages> imatges;

    /**
     * Ubicación asociada al vehículo, enlazada mediante código postal.
     */
    @ManyToOne
    @JoinColumn(name = "codi_postal", nullable = false)
    private Localitzacio localitzacio;

    /**
     * Lista de reservas asociadas al vehículo.
     */
    @OneToMany(mappedBy = "vehicle")
    private List<Reserva> reservas;

    /**
     * Lista de incidencias registradas para el vehículo.
     */
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private List<Incidencia> incidencias;

    /**
     * Tipo del vehículo (e.g., turismo, furgoneta, SUV), definido mediante un
     * enumerador.
     */
    @Enumerated(EnumType.STRING)
    private TipusVehicle tipusVehicle;

    // GETTERS Y SETTERS
    /**
     * Obtiene la matrícula del vehículo.
     *
     * @return Matrícula del vehículo.
     */
    public String getMatricula() {
        return matricula;
    }
    
    
    /**
     * Establece la matrícula del vehículo.
     *
     * @param matricula Nueva matrícula del vehículo.
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtiene la marca del vehículo.
     *
     * @return Marca del vehículo.
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Establece la marca del vehículo.
     *
     * @param marca Nueva marca del vehículo.
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * Obtiene el modelo del vehículo.
     *
     * @return Modelo del vehículo.
     */
    public String getModel() {
        return model;
    }

    /**
     * Establece el modelo del vehículo.
     *
     * @param model Nuevo modelo del vehículo.
     */
    public void setModel(String model) {
        this.model = model;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    /**
     * Obtiene el año de fabricación del vehículo.
     *
     * @return Año de fabricación.
     */
    public int getAny() {
        return any;
    }

    /**
     * Establece el año de fabricación del vehículo.
     *
     * @param any Nuevo año de fabricación.
     */
    public void setAny(int any) {
        this.any = any;
    }

    /**
     * Obtiene el tipo de combustible del vehículo.
     *
     * @return Tipo de combustible.
     */
    public String getTipusCombustible() {
        return tipusCombustible;
    }

    /**
     * Establece el tipo de combustible del vehículo.
     *
     * @param tipusCombustible Nuevo tipo de combustible.
     */
    public void setTipusCombustible(String tipusCombustible) {
        this.tipusCombustible = tipusCombustible;
    }

    /**
     * Obtiene el precio por día del vehículo.
     *
     * @return Precio por día.
     */
    public double getPreuPerDia() {
        return preuPerDia;
    }

    /**
     * Establece el precio por día del vehículo.
     *
     * @param preuPerDia Nuevo precio por día.
     */
    public void setPreuPerDia(double preuPerDia) {
        this.preuPerDia = preuPerDia;
    }

    /**
     * Indica si el vehículo está activo.
     *
     * @return Estado de actividad del vehículo.
     */
    public boolean isActiu() {
        return actiu;
    }

    /**
     * Activa o desactiva el vehículo.
     *
     * @param actiu Nuevo estado del vehículo.
     */
    public void setActiu(boolean actiu) {
        this.actiu = actiu;
    }

    /**
     * Obtiene la fianza requerida del vehículo.
     *
     * @return Fianza requerida.
     */
    public double getFianca() {
        return fianca;
    }

    /**
     * Establece la fianza requerida del vehículo.
     *
     * @param fianca Nueva fianza.
     */
    public void setFianca(double fianca) {
        this.fianca = fianca;
    }

    /**
     * Obtiene la lista de imágenes del vehículo.
     *
     * @return Lista de imágenes.
     */
    public List<VehicleImages> getImatges() {
        return imatges;
    }

    /**
     * Establece la lista de imágenes del vehículo.
     *
     * @param imatges Nueva lista de imágenes.
     */
    public void setImatges(List<VehicleImages> imatges) {
        this.imatges = imatges;
    }

    /**
     * Obtiene la ubicación del vehículo.
     *
     * @return Ubicación del vehículo.
     */
    public Localitzacio getLocalitzacio() {
        return localitzacio;
    }

    /**
     * Establece la ubicación del vehículo.
     *
     * @param localitzacio Nueva ubicación.
     */
    public void setLocalitzacio(Localitzacio localitzacio) {
        this.localitzacio = localitzacio;
    }

    /**
     * Obtiene la lista de reservas asociadas al vehículo.
     *
     * @return Lista de reservas.
     */
    public List<Reserva> getReservas() {
        return reservas;
    }

    /**
     * Establece la lista de reservas asociadas al vehículo.
     *
     * @param reservas Nueva lista de reservas.
     */
    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    /**
     * Obtiene la lista de incidencias asociadas al vehículo.
     *
     * @return Lista de incidencias.
     */
    public List<Incidencia> getIncidencias() {
        return incidencias;
    }

    /**
     * Establece la lista de incidencias asociadas al vehículo.
     *
     * @param incidencias Nueva lista de incidencias.
     */
    public void setIncidencias(List<Incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    /**
     * Obtiene el tipo del vehículo.
     *
     * @return Tipo del vehículo.
     */
    public TipusVehicle getTipusVehicle() {
        return tipusVehicle;
    }

    /**
     * Establece el tipo del vehículo.
     *
     * @param tipusVehicle Nuevo tipo de vehículo.
     */
    public void setTipusVehicle(TipusVehicle tipusVehicle) {
        this.tipusVehicle = tipusVehicle;
    }
}
