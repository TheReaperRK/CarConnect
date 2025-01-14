package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Entity.MySQL.Enums.Nacionalitats;
import cat.copernic.CarConnect.Entity.MySQL.Enums.TipusClient;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Repository.MySQL.ClientRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Servicio que gestiona las operaciones relacionadas con los clientes.
 * Proporciona métodos para crear, actualizar, eliminar y validar clientes, así
 * como para obtener clientes por su DNI.
 */
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return Lista de clientes.
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     *
     * @param client El cliente a guardar.
     * @throws DniDuplicadoException Si el DNI ya está en uso por otro cliente.
     * @throws DniIncorrecteException Si el DNI proporcionado es incorrecto.
     */
    public void saveClient(Client client) throws DniDuplicadoException, DniIncorrecteException {
        validarClient(client.getDni());
        clientRepository.save(client);
    }

    /**
     * Obtiene un cliente por su DNI.
     *
     * @param dni El DNI del cliente a obtener.
     * @return El cliente con el DNI especificado.
     * @throws RuntimeException Si no se encuentra el cliente.
     */
    public Client getClientByDni(String dni) {
        return clientRepository.findById(dni).orElseThrow(() -> new RuntimeException("Client no trobat"));
    }
    public Client getClientAutenticat(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        List<Client> clients = clientRepository.findByEmail(username);
        Client client = new Client();
       if(!clients.isEmpty()){
           client = clients.getFirst();
       }else client = null;
       
        //clientRepository.findById(dni).orElseThrow(() -> new RuntimeException("Client no trobat"));
        
        return client;
    }
    /**
     * Actualiza la información de un cliente existente.
     *
     * @param dni El DNI del cliente a actualizar.
     * @param updatedClient El cliente con los datos actualizados.
     */
    public void updateClient(String dni, Client updatedClient) {
        Client client = getClientByDni(dni);
        client.setNombre(updatedClient.getNombre());
        client.setApellido(updatedClient.getApellido());
        client.setEmail(updatedClient.getEmail());
        client.setPassword(updatedClient.getPassword());
        client.setTelefono(updatedClient.getTelefono());
        client.setCaducitatDni(updatedClient.getCaducitatDni());
        client.setLlicenciaConduccio(updatedClient.getLlicenciaConduccio());
        client.setTargetaCredit(updatedClient.getTargetaCredit());
        client.setCaducitatLlicenciaConduccio(updatedClient.getCaducitatLlicenciaConduccio());
        client.setAdreca(updatedClient.getAdreca());
        client.setTipusClient(updatedClient.getTipusClient());
        clientRepository.save(client);
    }

    /**
     * Elimina un cliente por su DNI.
     *
     * @param dni El DNI del cliente a eliminar.
     */
    public void deleteClient(String dni) {
        clientRepository.deleteById(dni);
    }

    /**
     * Filtra los clientes según varios parámetros.
     *
     * @param llicenciaConduccio La licencia de conducción por la que filtrar.
     * @param tipusClient El tipo de cliente por el que filtrar.
     * @param email El correo electrónico por el que filtrar.
     * @return Lista de clientes que coinciden con los criterios de filtrado.
     */
    public List<Client> filterClients(String llicenciaConduccio, Optional<TipusClient> tipusClient, String email, Optional<Nacionalitats> nacionalitat, String telefono) {
        return clientRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtrar por llicenciaConduccio si no es null
            if (llicenciaConduccio != null && !llicenciaConduccio.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("llicenciaConduccio"), "%" + llicenciaConduccio + "%"));
            }

            // Filtrar por tipusClient si está presente
            tipusClient.ifPresent(value
                    -> predicates.add(criteriaBuilder.equal(root.get("tipusClient"), value))
            );

            // Filtrar por mail si no es null
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }

            // Filtrar por nacionalitat si está presente
            nacionalitat.ifPresent(value
                    -> predicates.add(criteriaBuilder.equal(root.get("nacionalitat"), value))
            );

            // Filtrar por telefon si no es null
            if (telefono != null && !telefono.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("telefono"), "%" + telefono + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * Valida el DNI de un cliente.
     *
     * @param dni El DNI a validar.
     * @throws DniDuplicadoException Si el DNI ya está en uso por otro cliente.
     * @throws DniIncorrecteException Si el DNI es incorrecto.
     */
    public void validarClient(String dni) {
        if (clientRepository.existsById(dni)) { // Busca si existe el cliente
            throw new DniDuplicadoException("El DNI ja està en ús per un client.");
        } else if (!validadorDNI(dni)) {
            throw new DniIncorrecteException("El DNI és incorrecte.");
        }
    }

    /**
     * Valida el formato del DNI español y comprueba si la letra es correcta.
     *
     * @param dni El DNI a validar.
     * @return true si el DNI es válido, false en caso contrario.
     */
    public boolean validadorDNI(String dni) {
        Pattern patro = Pattern.compile("^\\d{8}[A-Z]$");
        Matcher matcher = patro.matcher(dni);

        if (matcher.matches()) {
            int n = Integer.parseInt(dni.substring(0, 8));
            String lletres = "TRWAGMYFPDXBNJZSQVHLCKE";
            char lletra = lletres.charAt(n % 23);
            if (dni.charAt(8) == lletra) {
                return true;
            }
        }
        return false;
    }

    public Client getClientByEmail(String email) {
        // Recuperar la lista de clientes con el email dado
        List<Client> clients = clientRepository.findByEmail(email);

        if (clients == null || clients.isEmpty()) {
            throw new RuntimeException("No se encontró ningún cliente con el email: " + email);
        }

        if (clients.size() > 1) {
            throw new RuntimeException("Hay múltiples clientes con el email: " + email);
        }

        // Retornar el único cliente encontrado
        return clients.get(0);
    }

}
