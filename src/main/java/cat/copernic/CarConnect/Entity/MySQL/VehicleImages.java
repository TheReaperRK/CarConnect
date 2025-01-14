/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Entity.MySQL;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Clase que representa las imágenes asociadas a un vehículo.
 * Se utiliza para almacenar el contenido binario de las imágenes y asociarlas 
 * a un vehículo específico mediante una relación ManyToOne.
 * 
 * @author Carlos
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VehicleImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data; // Contenido binario de la imagen

    private String nombre; // Nombre o descripción de la imagen

    @ManyToOne
    @JoinColumn(name = "vehicle_matricula", nullable = false)
    private Vehicle vehicle;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Vehicle getVehiculo() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehiculo) {
        this.vehicle = vehiculo;
    }
}
