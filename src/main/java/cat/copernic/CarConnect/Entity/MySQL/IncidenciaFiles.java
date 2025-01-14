/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Entity.MySQL;

/**
 *
 * @author Carlos
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Lob;


//@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "incidencia_files")
public class IncidenciaFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData; // Archivo en formato binario

    private String description;

    private String fileType; // Tipo de archivo (ej. "image/png", "image/jpeg")

    @ManyToOne
    @JoinColumn(name = "incidencia_id", nullable = false)
    private Incidencia incidencia; // Relación con Incidencia
    
    
    //GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Incidencia incidencia) {
        this.incidencia = incidencia;
    }
    
    
}

