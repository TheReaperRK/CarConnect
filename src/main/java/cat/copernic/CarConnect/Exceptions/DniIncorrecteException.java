package cat.copernic.CarConnect.Exceptions;

/**
 * Excepción personalizada que se lanza cuando el DNI proporcionado es
 * incorrecto. Esta excepción se extiende de {@link RuntimeException}, por lo
 * que es una excepción no verificada, es decir, no es obligatorio manejarla
 * explícitamente en el código con un bloque try-catch.
 *
 * Se utiliza para notificar que el formato o los datos del DNI proporcionado no
 * son válidos o no cumplen con los requisitos establecidos en el sistema.
 *
 * @author david
 */
public class DniIncorrecteException extends RuntimeException {

    /**
     * Constructor para la excepción DniIncorrecteException.
     *
     * @param message Mensaje de error que describe el motivo de la excepción.
     */
    public DniIncorrecteException(String message) {
        super(message);
    }
}
