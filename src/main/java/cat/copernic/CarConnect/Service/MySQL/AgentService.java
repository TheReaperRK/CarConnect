package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Exceptions.DniDuplicadoException;
import cat.copernic.CarConnect.Exceptions.DniIncorrecteException;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Repository.MySQL.ClientRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio que gestiona las operaciones relacionadas con los agentes.
 * Proporciona métodos para crear, actualizar, eliminar y validar agentes, así
 * como para obtener agentes por su DNI.
 */
@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Obtiene todos los agentes registrados.
     *
     * @return Lista de agentes.
     */
    public List<Agent> llistarAgents() {
        return agentRepository.findAll(); // Cambié de Client a Agent
    }
    
    /**
     * Obtiene un agente por su DNI.
     *
     * @param dni El DNI del agente a obtener.
     * @return El agente con el DNI especificado.
     * @throws RuntimeException Si no se encuentra el agente.
     */
    public Agent obtenirAgentPerDni(String dni) {
        return agentRepository.findById(dni).orElseThrow(() -> new RuntimeException("Agent no trobat"));
    }

    /**
     * Crea un nuevo agente y lo guarda en la base de datos.
     *
     * @param agent El agente a crear.
     * @throws DniDuplicadoException Si el DNI ya está en uso por otro agente o
     * cliente.
     * @throws DniIncorrecteException Si el DNI proporcionado es incorrecto.
     */
    public void crearAgent(Agent agent) throws DniDuplicadoException, DniIncorrecteException {
        validarAgent(agent.getDni());
        agent.setPermisosSegunRol();
        agentRepository.saveAndFlush(agent); // Cambié de Client a Agent
    }

    /**
     * Actualiza la información de un agente existente.
     *
     * @param dni El DNI del agente a actualizar.
     * @param agentActualitzat El agente con los datos actualizados.
     * @throws DniDuplicadoException Si el DNI ya está en uso por otro agente o
     * cliente.
     * @throws DniIncorrecteException Si el DNI proporcionado es incorrecto.
     */
    public void actualitzarAgent(String dni, Agent agentActualitzat) throws DniIncorrecteException, DniDuplicadoException {

        Agent agent = obtenirAgentPerDni(dni);

        agent.setNombre(agentActualitzat.getNombre());
        agent.setApellido(agentActualitzat.getApellido());
        agent.setEmail(agentActualitzat.getEmail());
        agent.setPassword(agentActualitzat.getPassword());
        agent.setTelefono(agentActualitzat.getTelefono());
        agent.setCaducitatDni(agentActualitzat.getCaducitatDni());
        agent.setLlicenciaConduccio(agentActualitzat.getLlicenciaConduccio());
        agent.setTargetaCredit(agentActualitzat.getTargetaCredit());
        agent.setCaducitatLlicenciaConduccio(agentActualitzat.getCaducitatLlicenciaConduccio());
        agent.setAdreca(agentActualitzat.getAdreca());
        agent.setTipusClient(agentActualitzat.getTipusClient());
        agent.setDataContracte(agentActualitzat.getDataContracte());
        agent.setRol(agentActualitzat.getRol());
        agent.setLocalitzacio(agentActualitzat.getLocalitzacio());
        agent.setPermisosSegunRol();
        // Si el DNI ha cambiado, se valida y se actualiza el agente
        if (!(agent.getDni().equals(agentActualitzat.getDni()))) {
            if (!validadorDNI(agentActualitzat.getDni())) {
                throw new DniIncorrecteException("El DNI és incorrecte.");
            } else {
                validarAgent(agentActualitzat.getDni());
                eliminarAgent(dni);
                agentRepository.save(agentActualitzat);
            }
        } else {
            agentRepository.save(agent);
        }
        // Actualizar otras relaciones o campos si es necesario
    }

    /**
     * Elimina un agente por su DNI.
     *
     * @param dni El DNI del agente a eliminar.
     * @return true si el agente fue eliminado, false si no se encontró.
     */
    public boolean eliminarAgent(String dni) {
        if (agentRepository.existsById(dni)) { // Busca si existe el agente
            agentRepository.deleteById(dni); // Eliminar el agente
            return true;
        }
        return false;
    }

    /**
     * Valida si el DNI de un agente es válido.
     *
     * @param dni El DNI a validar.
     * @throws DniDuplicadoException Si el DNI ya está en uso por otro agente o
     * cliente.
     * @throws DniIncorrecteException Si el DNI es incorrecto.
     */
    public void validarAgent(String dni) {

        if (agentRepository.existsById(dni)) { // Busca si existe el agente
            throw new DniDuplicadoException("El DNI ja està en ús per un agent.");
        } else if (clientRepository.existsById(dni)) { // Busca si existe el cliente
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

    /**
     * Filtra los agentes por su DNI.
     *
     * @param dni El DNI del agente a buscar. Si es null o vacío, se devuelven
     * todos los agentes.
     * @return Lista de agentes que coinciden con el DNI proporcionado.
     */
    public List<Agent> filterAgents(String dni) {

        List<Agent> agents = new ArrayList<>();
        if (dni != null && !dni.isEmpty()) {
            Optional<Agent> agent = agentRepository.findByDni(dni);
            if (agent.isPresent()) {
                agents.add(agent.get());
            }
        } else {
            return agentRepository.findAll();
        }

        return agents;
    }
}
