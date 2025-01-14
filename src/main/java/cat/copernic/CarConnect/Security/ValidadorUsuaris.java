package cat.copernic.CarConnect.Security;

import cat.copernic.CarConnect.Entity.MySQL.Client;
import cat.copernic.CarConnect.Repository.MySQL.ClientRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio para validar usuarios basado únicamente en clientes.
 * 
 * @author david
 */
@Service
public class ValidadorUsuaris implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepo;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar clientes por correo electrónico
        List<Client> llistaClient = clientRepo.findByEmail(username);

        if (llistaClient.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }


        // Retornar el cliente como un objeto UserDetails
        return llistaClient.get(0); // Asegúrate de que la clase Client implemente UserDetails.
    }
}
