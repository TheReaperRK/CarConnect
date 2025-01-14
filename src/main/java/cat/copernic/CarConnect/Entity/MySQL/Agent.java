/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Entity.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Enums.Rol;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusClient;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusPermis;
import cat.copernic.CarConnect.Security.Permis;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;

/**
 * Representa un agente dentro del sistema CarConnect.
 * <p>
 * Hereda de la clase {@link Client}, y añade propiedades específicas como la
 * fecha de contrato, el rol, y la localización asociada.
 * </p>
 *
 * <ul>
 * <li>Incluye enumeraciones que determinan el rol del agente.</li>
 * <li>Proporciona funcionalidad para gestionar permisos y localización.</li>
 * </ul>
 *
 * @see Client
 * @see Rol
 * @see TipusPermis
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Agent extends Client {

    /**
     * Fecha de inicio del contrato del agente.
     */
    private LocalDate dataContracte;

    /**
     * Rol asignado al agente dentro del sistema. Es una enumeración del tipo
     * {@link Rol}.
     */
    @Enumerated(EnumType.STRING)
    private Rol rol;

    /**
     * Localización asociada al agente, representada mediante la clase
     * {@link Localitzacio}. Puede ser nula.
     */
    @OneToOne
    @JoinColumn(name = "codi_postal", nullable = true)
    private Localitzacio localitzacio;

    /*
     * Lista de permisos asociados al agente.
     * Esta propiedad está comentada pero se utilizaba para definir 
     * una cadena de permisos separados por comas.
     */
    //private String llistaPermisos = permisosSegunRol();
           
     

    /*
     * Constructor para inicializar un objeto de tipo Agent.
     * 
     * @param dataContracte Fecha de contrato del agente.
     * @param rol Rol asignado al agente.
     * @param localitzacio Localización del agente.
     * @param caducitatDni Fecha de caducidad del DNI del agente.
     * @param llicenciaConduccio Número de licencia de conducción.
     * @param targetaCredit Número de tarjeta de crédito.
     * @param caducitatLlicenciaConduccio Fecha de caducidad de la licencia de conducción.
     * @param adreca Dirección del agente.
     * @param tipusClient Tipo de cliente asociado al agente.
     * @param reserves Reservas asignadas al agente.
     */
 

    /*
     * Retorna una colección de permisos asociados al agente.
     * 
     * @return Colección de permisos como objetos de tipo {@link GrantedAuthority}.
     */
 /*
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
    */
    
     
    /**
     * Obtiene la fecha de contrato del agente.
     *
     * @return Fecha de contrato.
     */
    public LocalDate getDataContracte() {
        return dataContracte;
    }

    /**
     * Establece la fecha de contrato del agente.
     *
     * @param dataContracte Nueva fecha de contrato.
     */
    public void setDataContracte(LocalDate dataContracte) {
        this.dataContracte = dataContracte;
    }

    /**
     * Obtiene el rol asignado al agente.
     *
     * @return Rol del agente.
     */
    public Rol getRol() {
        return rol;
    }

    /**
     * Establece el rol del agente.
     *
     * @param rol Nuevo rol para el agente.
     */
    public void setRol(Rol rol) {
        this.rol = rol;
        
    }

    /**
     * Obtiene la localización asociada al agente.
     *
     * @return Localización del agente.
     */
    public Localitzacio getLocalitzacio() {
        return localitzacio;
    }

    /**
     * Establece la localización asociada al agente.
     *
     * @param localitzacio Nueva localización del agente.
     */
    public void setLocalitzacio(Localitzacio localitzacio) {
        this.localitzacio = localitzacio;
    }

    /*
     * Obtiene el nombre de usuario del agente, equivalente a su email.
     * 
     * @return Email del agente.
     */
 
    @Override
    public String getUsername() {
        return super.getEmail();
    }
     

    /*
     * Obtiene la contraseña del agente.
     * 
     * @return Contraseña del agente.
     */
 
    @Override
    public String getPassword() {
        return super.getPassword();
    }
    
     /*
     * Pone los permisos del agente segun su rol.
     * 
     * 
     */
 
    public void setPermisosSegunRol(){
        
        String permisos =
            TipusPermis.HISTORIC_RESERVES.toString()+","+
            TipusPermis.HISTORIC_INCIDENCIES.toString()+","+
            TipusPermis.CREAR_RESERVES.toString()+","+
            TipusPermis.LLISTAR_RESERVES.toString()+","+
            TipusPermis.EDITAR_RESERVES.toString()+","+
            TipusPermis.ELIMINAR_RESERVES.toString()+","+
            TipusPermis.CREAR_VEHICLE.toString()+","+
            TipusPermis.LLISTAR_VEHICLE.toString()+","+
            TipusPermis.EDITAR_VEHICLE.toString()+","+
            TipusPermis.ELIMINAR_VEHICLE.toString()+","+
            TipusPermis.CREAR_INCIDENCIES.toString()+","+
            TipusPermis.LLISTAR_INCIDENCIES.toString()+","+
            TipusPermis.EDITAR_INCIDENCIES.toString()+","+
            TipusPermis.ELIMINAR_INCIDENCIES.toString()
            ;
        if(this.rol!=null && this.rol.equals(Rol.ADMINISTRADOR) ){    
            permisos += ","+TipusPermis.ADMIN.toString();
        }
        super.setLlistaPermisos(permisos);
       
    }
     
}
