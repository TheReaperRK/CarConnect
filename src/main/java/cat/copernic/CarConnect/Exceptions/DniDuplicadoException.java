package cat.copernic.CarConnect.Exceptions;

/**
 * Excepción personalizada que se lanza cuando se detecta un número de DNI
 * duplicado en el sistema. Esta excepción se extiende de
 * {@link RuntimeException}, por lo que es una excepción no verificada, es
 * decir, no es obligatorio manejarla explícitamente en el código con un bloque
 * try-catch.
 *
 * Esta excepción se utiliza para notificar que el DNI proporcionado ya existe
 * en la base de datos y no puede ser procesado de nuevo.
 *
 * @author david
 */
public class DniDuplicadoException extends RuntimeException {

    /**
     * Constructor para la excepción DniDuplicadoException.
     *
     * @param message Mensaje de error que describe el motivo de la excepción.
     */
    public DniDuplicadoException(String message) {
        super(message);
    }
}
