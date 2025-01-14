package cat.copernic.CarConnect.Entity.MySQL;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Clase base para representar usuarios en el sistema CarConnect.
 * <p>
 * Esta clase abstracta incluye los atributos y métodos comunes para las
 * entidades relacionadas con usuarios, como clientes o agentes.
 * </p>
 *
 * @author David
 */
@MappedSuperclass
public abstract class Usuario implements UserDetails {

    /**
     * DNI del usuario. Este atributo es único y actúa como clave primaria.
     */
    @Id
    @Column(unique = true, nullable = false)
    private String dni;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Apellido del usuario.
     */
    private String apellido;

    /**
     * Correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Número de teléfono de contacto del usuario.
     */
    private String telefono;

    // MÉTODOS GETTERS Y SETTERS
    /**
     * Obtiene el DNI del usuario.
     *
     * @return DNI del usuario.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del usuario.
     *
     * @param dni Nuevo DNI del usuario.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre Nuevo nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return Apellido del usuario.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     *
     * @param apellido Nuevo apellido del usuario.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email Nuevo correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password Nueva contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     *
     * @return Número de teléfono del usuario.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param telefono Nuevo número de teléfono del usuario.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
