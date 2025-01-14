package cat.copernic.CarConnect.Repository.MySQL;

import cat.copernic.CarConnect.Entity.MySQL.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, String>, JpaSpecificationExecutor<Client> {

    Optional<Client> findByDni(String dni);

    @Query("SELECT c FROM Client c WHERE c.email = :email")
    List<Client> findByEmail(
            @Param("email") String email
    );
    
    
    //List<Client> findById(String dni);

}
