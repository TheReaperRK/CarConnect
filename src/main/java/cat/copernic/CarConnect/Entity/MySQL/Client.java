/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Entity.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Enums.Nacionalitats;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusClient;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusPermis;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusLlicenciaConduccio;
import cat.copernic.CarConnect.Security.Permis;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Representa un cliente dentro del sistema CarConnect.
 * <p>
 * Hereda de la clase {@link Usuario} y contiene información específica del
 * cliente, como la caducidad de su DNI, licencia de conducción, tipo de
 * cliente, entre otros. Además, puede tener una lista de reservas asociadas.
 * </p>
 *
 * @see Usuario
 * @see TipusClient
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Client extends Usuario implements UserDetails  {

    /**
     * Fecha de caducidad del DNI del cliente.
     */
    private LocalDate caducitatDni;

    /**
     * Número de licencia de conducción del cliente.
     */
    
    private String llicenciaConduccio;

    /**
     * Número de tarjeta de crédito del cliente.
     */
    private String targetaCredit;

    /**
     * Fecha de caducidad de la licencia de conducción.
     */
    private LocalDate caducitatLlicenciaConduccio;

    /**
     * Dirección del cliente.
     */
    private String adreca;

    /**
     * Tipo de cliente, definido por la enumeración {@link TipusClient}.
     */
    @Enumerated(EnumType.STRING)
    private TipusClient tipusClient;

    @Enumerated(EnumType.STRING)
    private Nacionalitats nacionalitat;
    
    /**
     * Lista de reservas asociadas al cliente. Cada reserva está mapeada con la
     * propiedad "client" en la clase {@link Reserva}.
     */
    @OneToMany(mappedBy = "client")
    private List<Reserva> reserves;
            
    private String llistaPermisos = 
            TipusPermis.CREAR_RESERVES.toString()+","+
            TipusPermis.LLISTAR_RESERVES.toString();
     

    /*
     * Constructor para inicializar un objeto de tipo Client.
     * 
     * @param caducitatDni Fecha de caducidad del DNI.
     * @param llicenciaConduccio Número de licencia de conducción.
     * @param targetaCredit Número de tarjeta de crédito.
     * @param caducitatLlicenciaConduccio Fecha de caducidad de la licencia de conducción.
     * @param adreca Dirección del cliente.
     * @param tipusClient Tipo de cliente.
     * @param reserves Lista de reservas asociadas al cliente.
     */

     @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        List<GrantedAuthority> ret = new ArrayList<>();
        
        String[] llista1 = llistaPermisos.split(",");
        
        for (String p : llista1)
        {
            ret.add(new Permis(TipusPermis.valueOf(p)));
        }
        
        return ret;
    }
    
    // GETTERS Y SETTERS
    
    public LocalDate getCaducitatDni() {
        return caducitatDni;
    }

    /**
     * Establece la fecha de caducidad del DNI del cliente.
     *
     * @param caducitatDni Nueva fecha de caducidad del DNI.
     */
    public void setCaducitatDni(LocalDate caducitatDni) {
        this.caducitatDni = caducitatDni;
    }

    /**
     * Obtiene el número de licencia de conducción del cliente.
     *
     * @return Número de licencia de conducción.
     */
    public String getLlicenciaConduccio() {
        return llicenciaConduccio;
    }

    /**
     * Establece el número de licencia de conducción del cliente.
     *
     * @param llicenciaConduccio Nuevo número de licencia de conducción.
     */
    public void setLlicenciaConduccio(String llicenciaConduccio) {
        this.llicenciaConduccio = llicenciaConduccio;
    }

    /**
     * Obtiene el número de tarjeta de crédito del cliente.
     *
     * @return Número de tarjeta de crédito.
     */
    public String getTargetaCredit() {
        return targetaCredit;
    }

    /**
     * Establece el número de tarjeta de crédito del cliente.
     *
     * @param targetaCredit Nuevo número de tarjeta de crédito.
     */
    public void setTargetaCredit(String targetaCredit) {
        this.targetaCredit = targetaCredit;
    }

    /**
     * Obtiene la fecha de caducidad de la licencia de conducción.
     *
     * @return Fecha de caducidad de la licencia de conducción.
     */
    public LocalDate getCaducitatLlicenciaConduccio() {
        return caducitatLlicenciaConduccio;
    }

    /**
     * Establece la fecha de caducidad de la licencia de conducción.
     *
     * @param caducitatLlicenciaConduccio Nueva fecha de caducidad de la
     * licencia de conducción.
     */
    public void setCaducitatLlicenciaConduccio(LocalDate caducitatLlicenciaConduccio) {
        this.caducitatLlicenciaConduccio = caducitatLlicenciaConduccio;
    }

    /**
     * Obtiene la dirección del cliente.
     *
     * @return Dirección del cliente.
     */
    public String getAdreca() {
        return adreca;
    }

    /**
     * Establece la dirección del cliente.
     *
     * @param adreca Nueva dirección del cliente.
     */
    public void setAdreca(String adreca) {
        this.adreca = adreca;
    }

    /**
     * Obtiene el tipo de cliente.
     *
     * @return Tipo de cliente.
     */
    public TipusClient getTipusClient() {
        return tipusClient;
    }

    /**
     * Establece el tipo de cliente.
     *
     * @param tipusClient Nuevo tipo de cliente.
     */
    public void setTipusClient(TipusClient tipusClient) {
        this.tipusClient = tipusClient;
    }

    /**
     * Obtiene la lista de reservas asociadas al cliente.
     *
     * @return Lista de reservas.
     */
    public List<Reserva> getReserves() {
        return reserves;
    }

    /**
     * Establece la lista de reservas asociadas al cliente.
     *
     * @param reserves Nueva lista de reservas.
     */
    public void setReserves(List<Reserva> reserves) {
        this.reserves = reserves;
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }
     

 /*
     * Obtiene la contraseña del cliente.
     * 
     * @return Contraseña del cliente.
     */
 
    @Override
    public String getPassword() {
        return super.getPassword();
    }
    
    
    public String getLlistaPermisos() {
        return llistaPermisos;
    }

    public void setLlistaPermisos(String llistaPermisos) {
        this.llistaPermisos = llistaPermisos;
    }

    /**
     * Obtiene la nacionalidad del cliente.
     *
     * @return Nacionalitat
     */
    
    public Nacionalitats getNacionalitat() {
        return nacionalitat;
    }

     /**
     * Establece la nacionalidad de un Cliente.
     *
     * @param Nueva nacionalidad
     */
    public void setNacionalitat(Nacionalitats nacionalitat) {
        this.nacionalitat = nacionalitat;
    }
    
   
}
