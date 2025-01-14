package cat.copernic.CarConnect.Service.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Agent;
import cat.copernic.CarConnect.Entity.MySQL.Localitzacio;
import cat.copernic.CarConnect.Repository.MySQL.AgentRepository;
import cat.copernic.CarConnect.Repository.MySQL.LocalitzacioRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Servicio que gestiona las localizaciones en el sistema. Proporciona métodos
 * para crear, modificar, eliminar y consultar localizaciones.
 */
@Service
public class LocalitzacioService {

    @Autowired
    private LocalitzacioRepository localitzacioRepository;
    
    @Autowired
    private AgentRepository agentRepo;
    /**
     * Obtiene todas las localizaciones almacenadas en el sistema.
     *
     * @return Una lista con todas las localizaciones.
     */
    public List<Localitzacio> consultarLocalitzacions() {
        return localitzacioRepository.findAll();
    }
    

    /**
     * Crea una nueva localización. Si ya existe una localización con el mismo
     * código postal, se lanza una excepción.
     *
     * @param localitzacio La localización a crear.
     * @return La localización creada.
     * @throws IllegalArgumentException Si ya existe una localización con el
     * mismo código postal.
     */
    public Localitzacio crearLocalitzacio(Localitzacio localitzacio) {
        if (localitzacioRepository.existsById(localitzacio.getCodiPostal())) {
            throw new IllegalArgumentException("Ja existeix una localització amb aquest codi postal.");
        }
        return localitzacioRepository.save(localitzacio);
    }

    /**
     * Modifica una localización existente. Se obtiene la localización
     * utilizando el código postal antiguo y luego se guarda el nuevo objeto con
     * el código postal actualizado.
     *
     * @param codiPostalAntic El código postal antiguo de la localización a
     * modificar.
     * @param localitzacio El objeto de localización con los datos actualizados.
     * @throws IllegalArgumentException Si no se encuentra la localización con
     * el código postal antiguo.
     */
    public void modificarLocalitzacio(String codiPostalAntic, Localitzacio localitzacio) {
        // Primero obtener la localización existente
        Localitzacio localitzacioExistente = localitzacioRepository.findById(codiPostalAntic)
                .orElseThrow(() -> new IllegalArgumentException("Localització no trobada."));

        // Eliminar la entidad existente
        localitzacioRepository.delete(localitzacioExistente);

        // Guardar la nueva localización con el nuevo CodiPostal
        // Como ya tienes el nuevo CodiPostal en el objeto localitzacio, simplemente guardamos el nuevo objeto
        localitzacioRepository.save(localitzacio);
    }

    /**
     * Elimina una localización del sistema. Si la localización no existe, se
     * lanza una excepción.
     *
     * @param codiPostal El código postal de la localización a eliminar.
     * @throws IllegalArgumentException Si la localización con el código postal
     * no existe.
     */
    public void eliminarLocalitzacio(String codiPostal) {
        if (!localitzacioRepository.existsById(codiPostal)) {
            throw new IllegalArgumentException("La localització especificada no existeix.");
        }
        localitzacioRepository.deleteById(codiPostal);
    }

    /**
     * Filtra las localizaciones en el sistema según los parámetros
     * proporcionados. Los parámetros son opcionales, y si se proporcionan, se
     * filtran las localizaciones que coinciden con los valores dados.
     *
     * @param codiPostal El código postal a filtrar (opcional).
     * @param comunitatAutonoma La comunidad autónoma a filtrar (opcional).
     * @param provincia La provincia a filtrar (opcional).
     * @param ciutat La ciudad a filtrar (opcional).
     * @return Una lista de localizaciones que coinciden con los filtros
     * proporcionados.
     */
    public List<Localitzacio> filtrarLocalitzacio(String codiPostal, String comunitatAutonoma, String provincia, String ciutat) {
        return localitzacioRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtrar por codiPostal si no es null
            if (codiPostal != null && !codiPostal.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("codiPostal"), "%" + codiPostal + "%"));
            }

            // Filtrar por comunitatAutonoma si no es null
            if (comunitatAutonoma != null && !comunitatAutonoma.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("comunitatAutonoma"), "%" + comunitatAutonoma + "%"));
            }

            // Filtrar por provincia si no es null
            if (provincia != null && !provincia.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("provincia"), "%" + provincia + "%"));
            }

            // Filtrar por ciutat si no es null
            if (ciutat != null && !ciutat.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("ciutat"), "%" + ciutat + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

}
