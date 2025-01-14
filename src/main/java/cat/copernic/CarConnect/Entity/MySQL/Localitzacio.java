/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.CarConnect.Entity.MySQL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.*;

/**
 * Representa una localización dentro del sistema CarConnect.
 * <p>
 * Contiene información detallada sobre la dirección y se asocia tanto con
 * vehículos como con un agente.
 * </p>
 *
 * @author david
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Localitzacio {

    /**
     * Código postal único que identifica la localización.
     */
    @Id
    @Column(unique = true, nullable = false)
    private String codiPostal;

    /**
     * Comunidad autónoma de la localización.
     */
    @Column(nullable = false)
    private String comunitatAutonoma;

    /**
     * Provincia de la localización.
     */
    @Column(nullable = false)
    private String provincia;

    /**
     * Ciudad donde se encuentra la localización.
     */
    @Column(nullable = false)
    private String ciutat;

    /**
     * Calle de la dirección de la localización.
     */
    @Column(nullable = false)
    private String carrer;

    /**
     * Número de la calle en la dirección de la localización.
     */
    @Column(nullable = false)
    private int num;

    /**
     * Lista de vehículos asociados a esta localización.
     */
    @OneToMany(mappedBy = "localitzacio")
    private List<Vehicle> vehicle;

    /**
     * Agente asociado a esta localización. Representa una relación uno a uno.
     */
    @OneToOne(mappedBy = "localitzacio")
    private Agent agent;

    /**
     * Obtiene el código postal de la localización.
     *
     * @return Código postal.
     */
    public String getCodiPostal() {
        return codiPostal;
    }

    /**
     * Establece el código postal de la localización.
     *
     * @param codiPostal Nuevo código postal.
     */
    public void setCodiPostal(String codiPostal) {
        this.codiPostal = codiPostal;
    }

    /**
     * Obtiene la comunidad autónoma de la localización.
     *
     * @return Comunidad autónoma.
     */
    public String getComunitatAutonoma() {
        return comunitatAutonoma;
    }

    /**
     * Establece la comunidad autónoma de la localización.
     *
     * @param comunitatAutonoma Nueva comunidad autónoma.
     */
    public void setComunitatAutonoma(String comunitatAutonoma) {
        this.comunitatAutonoma = comunitatAutonoma;
    }

    /**
     * Obtiene la provincia de la localización.
     *
     * @return Provincia.
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia de la localización.
     *
     * @param provincia Nueva provincia.
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * Obtiene la ciudad de la localización.
     *
     * @return Ciudad.
     */
    public String getCiutat() {
        return ciutat;
    }

    /**
     * Establece la ciudad de la localización.
     *
     * @param ciutat Nueva ciudad.
     */
    public void setCiutat(String ciutat) {
        this.ciutat = ciutat;
    }

    /**
     * Obtiene la calle de la localización.
     *
     * @return Calle.
     */
    public String getCarrer() {
        return carrer;
    }

    /**
     * Establece la calle de la localización.
     *
     * @param carrer Nueva calle.
     */
    public void setCarrer(String carrer) {
        this.carrer = carrer;
    }

    /**
     * Obtiene el número de la calle de la localización.
     *
     * @return Número de la calle.
     */
    public int getNum() {
        return num;
    }

    /**
     * Establece el número de la calle de la localización.
     *
     * @param num Nuevo número de la calle.
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * Obtiene la lista de vehículos asociados a esta localización.
     *
     * @return Lista de vehículos.
     */
    public List<Vehicle> getVehicle() {
        return vehicle;
    }

    /**
     * Establece la lista de vehículos asociados a esta localización.
     *
     * @param vehicle Nueva lista de vehículos.
     */
    public void setVehicle(List<Vehicle> vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Obtiene el agente asociado a esta localización.
     *
     * @return Agente asociado.
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Establece el agente asociado a esta localización.
     *
     * @param agent Nuevo agente asociado.
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
