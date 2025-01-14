package cat.copernic.CarConnect.Entity.MongoDB;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;



/**
 * Clase que representa el historial de reservas asociadas a un cliente y un vehículo.
 * Se utiliza para almacenar información de reservas pasadas, incluyendo datos del cliente,
 * vehículo, fechas, precio total y estado de la reserva.
 * 
 * Este modelo está diseñado para persistirse en una colección de MongoDB.
 * 
 * @author Toni
 */

@Data

//@Data

@Document(collection = "historic_reserves")
public class HistoricReserves {

    @Id
    private String id; // ID único para el historial de reserva

    private Long reservaId; // ID de la reserva (relación con Reserva en MySQL)
    private String clientDni; // DNI del cliente relacionado
    private String vehicleId; // ID del vehículo relacionado
    private LocalDate startDate; // Fecha de inicio
    private LocalDate endDate; // Fecha de fin
    private double totalPrice; // Precio total de la reserva
    private String status; // Estado de la reserva
    private String email;
    private String telefon;
    private String nom;
    
    
    //getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public String getClientDni() {
        return clientDni;
    }

    public void setClientDni(String clientDni) {
        this.clientDni = clientDni;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    
}
