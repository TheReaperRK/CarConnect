package cat.copernic.CarConnect.Entity.MySQL;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Representa una reserva en el sistema CarConnect.
 * <p>
 * Cada reserva está asociada a un cliente y un vehículo, e incluye información
 * sobre las fechas de recogida y devolución, precio total, fianza y estado de
 * la reserva.
 * </p>
 *
 * @author Toni
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reserva {

    /**
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    /**
     * Cliente asociado a la reserva. Representa una relación muchos a uno con
     * la entidad {@link Client}.
     */
    @ManyToOne
    @JoinColumn(name = "client_dni", nullable = false)
    private Client client;

    /**
     * Vehículo asociado a la reserva. Representa una relación muchos a uno con
     * la entidad {@link Vehicle}.
     */
    @ManyToOne
    @JoinColumn(name = "vehicle_matricula", nullable = false)
    private Vehicle vehicle;

    /**
     * Fecha y hora de recogida del vehículo.
     */
    @Column(nullable = false)
    private LocalDate dataHoraRecollida;

    /**
     * Fecha y hora de devolución del vehículo.
     */
    @Column(nullable = false)
    private LocalDate dataHoraDevolucio;

    /**
     * Fecha de cancelación de la reserva, si corresponde.
     */
    private LocalDate dataCancelacio;

    private boolean retornat;
    
    private boolean lliurat;
    
    /**
     * Precio total de la reserva.
     */
    private double preuTotal;

    /**
     * Importe de la fianza asociada a la reserva.
     */
    private double fianca;

    /**
     * Indica si la reserva está activa.
     */
    private boolean actiu;

    /**
     * Tipo de seguro asociado a la reserva.
     */
    private String asseguranca;

    /**
     * Obtiene el identificador único de la reserva.
     *
     * @return Identificador de la reserva.
     */
    public Long getIdReserva() {
        return idReserva;
    }

    /**
     * Establece el identificador único de la reserva.
     *
     * @param idReserva Nuevo identificador de la reserva.
     */
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    /**
     * Obtiene el cliente asociado a la reserva.
     *
     * @return Cliente asociado.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Establece el cliente asociado a la reserva.
     *
     * @param client Nuevo cliente asociado.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Obtiene el vehículo asociado a la reserva.
     *
     * @return Vehículo asociado.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Establece el vehículo asociado a la reserva.
     *
     * @param vehicle Nuevo vehículo asociado.
     */
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Obtiene la fecha y hora de recogida del vehículo.
     *
     * @return Fecha y hora de recogida.
     */
    public LocalDate getDataHoraRecollida() {
        return dataHoraRecollida;
    }

    /**
     * Establece la fecha y hora de recogida del vehículo.
     *
     * @param dataHoraRecollida Nueva fecha y hora de recogida.
     */
    public void setDataHoraRecollida(LocalDate dataHoraRecollida) {
        this.dataHoraRecollida = dataHoraRecollida;
    }

    /**
     * Obtiene la fecha y hora de devolución del vehículo.
     *
     * @return Fecha y hora de devolución.
     */
    public LocalDate getDataHoraDevolucio() {
        return dataHoraDevolucio;
    }

    /**
     * Establece la fecha y hora de devolución del vehículo.
     *
     * @param dataHoraDevolucio Nueva fecha y hora de devolución.
     */
    public void setDataHoraDevolucio(LocalDate dataHoraDevolucio) {
        this.dataHoraDevolucio = dataHoraDevolucio;
    }

    /**
     * Obtiene la fecha de cancelación de la reserva.
     *
     * @return Fecha de cancelación.
     */
    public LocalDate getDataCancelacio() {
        return dataCancelacio;
    }

    /**
     * Establece la fecha de cancelación de la reserva.
     *
     * @param dataCancelacio Nueva fecha de cancelación.
     */
    public void setDataCancelacio(LocalDate dataCancelacio) {
        this.dataCancelacio = dataCancelacio;
    }

    /**
     * Obtiene el precio total de la reserva.
     *
     * @return Precio total.
     */
    public double getPreuTotal() {
        return preuTotal;
    }

    /**
     * Establece el precio total de la reserva.
     *
     * @param preuTotal Nuevo precio total.
     */
    public void setPreuTotal(double preuTotal) {
        this.preuTotal = preuTotal;
    }

    /**
     * Obtiene la fianza de la reserva.
     *
     * @return Fianza de la reserva.
     */
    public double getFianca() {
        return fianca;
    }

    /**
     * Obtiene el estado de retornat.
     *
     * @return el estado de retornat.
     */
    public boolean isRetornat() {
        return retornat;
    }
    /**
     * Establece el estado de retornat.
     *
     * @param retornat
     */
    public void setRetornat(boolean retornat) {
        this.retornat = retornat;
    }

    /**
     * Obtiene el estado de lliurat.
     *
     * @return el estado de lliurat.
     */
    public boolean isLliurat() {
        return lliurat;
    }

    /**
     * Establece el estado de lliurat.
     *
     * @param lliurat
     */
    public void setLliurat(boolean lliurat) {
        this.lliurat = lliurat;
    }

    /**
     * Establece la fianza de la reserva.
     *
     * @param fianca Nueva fianza.
     */
    public void setFianca(double fianca) {
        this.fianca = fianca;
    }

    /**
     * Indica si la reserva está activa.
     *
     * @return {@code true} si la reserva está activa, de lo contrario
     * {@code false}.
     */
    public boolean isActiu() {
        return actiu;
    }

    /**
     * Establece el estado activo de la reserva.
     *
     * @param actiu Nuevo estado activo.
     */
    public void setActiu(boolean actiu) {
        this.actiu = actiu;
    }

    /**
     * Obtiene el tipo de seguro asociado a la reserva.
     *
     * @return Tipo de seguro.
     */
    public String getAsseguranca() {
        return asseguranca;
    }

    /**
     * Establece el tipo de seguro asociado a la reserva.
     *
     * @param asseguranca Nuevo tipo de seguro.
     */
    public void setAsseguranca(String asseguranca) {
        this.asseguranca = asseguranca;
    }
}
